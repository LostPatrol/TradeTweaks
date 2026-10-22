// Select exact release assets by Minecraft version and loader, including legacy releases.
'use strict';

// Historical unqualified names retain their original loader identity.
const targets = {
  '1.20.1': { nativeLoader: 'forge', java: '17' },
  '1.21.1': { nativeLoader: 'neoforge', java: '21' },
};
// Loader names are also used in user-facing release titles.
const loaderNames = { forge: 'Forge', neoforge: 'NeoForge', fabric: 'Fabric' };

/** Parse new loader-qualified names while retaining historical version identifiers. */
function identifyAsset(name) {
  const qualified = /^tradetweaks-(1\.20\.1|1\.21\.1)-(\d+\.\d+\.\d+(?:-[A-Za-z0-9.-]+)?)-(forge|neoforge|fabric)\.jar$/.exec(name);
  const legacy = qualified ? null : /^tradetweaks-(1\.20\.1|1\.21\.1)-(\d+\.\d+\.\d+(?:-[A-Za-z0-9.-]+)?)\.jar$/.exec(name);
  const match = qualified || legacy;
  if (!match) throw new Error(`Unrecognized production JAR: ${name}`);
  const [, minecraft, modVersion] = match;
  const target = targets[minecraft];
  const loader = qualified ? match[3] : target.nativeLoader;
  if (loader !== 'fabric' && loader !== target.nativeLoader) throw new Error(`Unsupported loader for ${minecraft}: ${loader}`);
  const version = `${minecraft}-${modVersion}`;
  return { minecraft, loader, java: target.java, version,
    versionId: qualified ? `${version}-${loader}` : version,
    displayName: `Trade Tweaks ${version}${qualified ? ` (${loaderNames[loader]})` : ''}` };
}

/** Reject ambiguous assets before any platform can receive an upload. */
function planRelease(release, assets, { branch = 'all', loader = 'all', platform = 'all' } = {}) {
  if (release.draft) throw new Error('Publish the GitHub Release before distributing it.');
  if (!release.body?.trim()) throw new Error('The Release changelog must not be empty.');
  if (branch !== 'all' && !targets[branch]) throw new Error('Unsupported branch filter.');
  if (loader !== 'all' && !loaderNames[loader]) throw new Error('Unsupported loader filter.');
  if (!['all', 'curseforge', 'modrinth'].includes(platform)) throw new Error('Unsupported platform filter.');
  const seen = new Set();
  const builds = [];
  for (const asset of assets) {
    if (!asset.name.endsWith('.jar')) continue;
    if (/-(sources|javadoc|dev)\.jar$/.test(asset.name)) continue;
    const identity = identifyAsset(asset.name);
    const key = `${identity.minecraft}/${identity.loader}`;
    if (seen.has(key)) throw new Error(`Multiple production JARs for ${key}.`);
    if (asset.state !== 'uploaded' || !asset.size) throw new Error(`Incomplete asset: ${asset.name}`);
    seen.add(key);
    builds.push({ ...identity,
      name: asset.name, assetId: asset.id, size: asset.size, digest: asset.digest || '',
      versionType: release.prerelease ? 'beta' : 'release' });
  }
  const selected = builds.filter(build => (branch === 'all' || build.minecraft === branch)
    && (loader === 'all' || build.loader === loader));
  if (!selected.length) throw new Error('No matching production JARs attached to this Release.');
  const platforms = platform === 'all' ? ['curseforge', 'modrinth'] : [platform];
  return { include: selected.flatMap(build => platforms.map(platform => ({ ...build, platform }))) };
}

module.exports = { planRelease, identifyAsset };
