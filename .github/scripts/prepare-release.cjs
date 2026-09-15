// Snapshot release assets and metadata once so every platform receives the same bytes.
'use strict';
const fs = require('node:fs');
const crypto = require('node:crypto');
const { planRelease } = require('./release-plan.cjs');

/** Called by actions/github-script with its authenticated GitHub client. */
module.exports = async ({ github, context, core }) => {
  const repo = context.repo;
  const release = context.eventName === 'release' ? context.payload.release
    : (await github.rest.repos.getReleaseByTag({ ...repo, tag: process.env.RELEASE_TAG })).data;
  const assets = await github.paginate(github.rest.repos.listReleaseAssets, { ...repo, release_id: release.id });
  const matrix = planRelease(release, assets, {
    branch: process.env.RELEASE_BRANCH || 'all', platform: process.env.RELEASE_PLATFORM || 'all',
  });
  fs.mkdirSync('release-assets', { recursive: true });
  for (const entry of new Map(matrix.include.map(entry => [entry.assetId, entry])).values()) {
    const response = await github.rest.repos.getReleaseAsset({ ...repo, asset_id: entry.assetId,
      headers: { accept: 'application/octet-stream' } });
    const bytes = Buffer.from(response.data);
    if (bytes.length !== entry.size) throw new Error(`Asset size mismatch: ${entry.name}`);
    const digest = `sha256:${crypto.createHash('sha256').update(bytes).digest('hex')}`;
    if (entry.digest && entry.digest !== digest) throw new Error(`Asset digest mismatch: ${entry.name}`);
    fs.writeFileSync(`release-assets/${entry.name}`, bytes);
    core.info(`${entry.name}: ${digest}`);
  }
  fs.writeFileSync('release-assets/changelog.md', release.body);
  fs.writeFileSync('release-assets/plan.json', JSON.stringify(matrix, null, 2));
  core.setOutput('matrix', JSON.stringify(matrix));
  await core.summary.addHeading(`Release ${release.tag_name}`)
    .addTable([['Asset', 'Minecraft', 'Loader', 'Platform'],
      ...matrix.include.map(e => [e.name, e.minecraft, e.loader, e.platform])]).write();
};
