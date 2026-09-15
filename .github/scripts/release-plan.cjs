// Derive independent platform uploads from the exact JAR assets on a GitHub Release.
'use strict';

const targets = {
  '1.20.1': { loader: 'forge', java: '17' },
  '1.21.1': { loader: 'neoforge', java: '21' },
};

/** Reject ambiguous assets before any platform can receive an upload. */
function planRelease(release, assets, { branch = 'all', platform = 'all' } = {}) {
  if (release.draft) throw new Error('Publish the GitHub Release before distributing it.');
  if (!release.body?.trim()) throw new Error('The Release changelog must not be empty.');
  if (branch !== 'all' && !targets[branch]) throw new Error('Unsupported branch filter.');
  if (!['all', 'curseforge', 'modrinth'].includes(platform)) throw new Error('Unsupported platform filter.');
  const seen = new Set();
  const builds = [];
  for (const asset of assets) {
    if (!asset.name.endsWith('.jar')) continue;
    if (/-(sources|javadoc|dev)\.jar$/.test(asset.name)) continue;
    const match = /^tradetweaks-(1\.20\.1|1\.21\.1)-(\d+\.\d+\.\d+(?:-[A-Za-z0-9.-]+)?)\.jar$/.exec(asset.name);
    if (!match) throw new Error(`Unrecognized production JAR: ${asset.name}`);
    const [, minecraft, version] = match;
    if (seen.has(minecraft)) throw new Error(`Multiple production JARs for ${minecraft}.`);
    if (asset.state !== 'uploaded' || !asset.size) throw new Error(`Incomplete asset: ${asset.name}`);
    seen.add(minecraft);
    builds.push({ minecraft, ...targets[minecraft], version: `${minecraft}-${version}`,
      name: asset.name, assetId: asset.id, size: asset.size, digest: asset.digest || '',
      versionType: release.prerelease ? 'beta' : 'release' });
  }
  const selected = builds.filter(build => branch === 'all' || build.minecraft === branch);
  if (!selected.length) throw new Error('No matching production JARs attached to this Release.');
  const platforms = platform === 'all' ? ['curseforge', 'modrinth'] : [platform];
  return { include: selected.flatMap(build => platforms.map(platform => ({ ...build, platform }))) };
}

module.exports = { planRelease };
