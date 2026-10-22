// Exercise real single/dual-branch release shapes and reject unsafe asset selections.
'use strict';
const { test } = require('node:test');
const assert = require('node:assert/strict');
const { planRelease } = require('./release-plan.cjs');
const prepareRelease = require('./prepare-release.cjs');
const fs = require('node:fs');
const crypto = require('node:crypto');
const release = { tag_name: '2.2.0', body: '- Fixed trading.', draft: false, prerelease: false };
const asset = (mc, v = '2.2.0') => ({ id: mc === '1.20.1' ? 1 : 2,
  name: `tradetweaks-${mc}-${v}.jar`, state: 'uploaded', size: 100 });

/** Build the loader-qualified asset shape used from 2.4.0 onward. */
function loaderAsset(mc, loader, version = '2.4.0') {
  return { ...asset(mc, version), id: `${mc}-${loader}`, name: `tradetweaks-${mc}-${version}-${loader}.jar` };
}

test('four builds create eight independently identifiable platform uploads', () => {
  const files = [loaderAsset('1.20.1', 'forge'), loaderAsset('1.20.1', 'fabric'),
    loaderAsset('1.21.1', 'neoforge'), loaderAsset('1.21.1', 'fabric')];
  const rows = planRelease(release, files).include;
  assert.equal(rows.length, 8);
  assert.equal(new Set(rows.map(row => row.versionId)).size, 4);
  assert.equal(rows[6].version, '1.21.1-2.4.0');
  assert.equal(rows[6].versionId, '1.21.1-2.4.0-fabric');
  assert.equal(rows[6].displayName, 'Trade Tweaks 1.21.1-2.4.0 (Fabric)');
});

test('retry selects one loader without reuploading the other loader', () => {
  const rows = planRelease(release, [loaderAsset('1.21.1', 'neoforge'), loaderAsset('1.21.1', 'fabric')],
    { branch: '1.21.1', loader: 'fabric', platform: 'modrinth' }).include;
  assert.equal(rows.length, 1);
  assert.equal(rows[0].loader, 'fabric');
  assert.equal(rows[0].platform, 'modrinth');
});

test('legacy assets retain published identities, including prereleases', () => {
  const row = planRelease(release, [asset('1.21.1', '2.3.0-beta.1')]).include[0];
  assert.equal(row.versionId, '1.21.1-2.3.0-beta.1');
  assert.equal(row.displayName, 'Trade Tweaks 1.21.1-2.3.0-beta.1');
  assert.equal(row.loader, 'neoforge');
});

test('qualified prereleases separate loader from version', () => {
  const row = planRelease({ ...release, prerelease: true },
    [loaderAsset('1.20.1', 'fabric', '2.4.0-beta.1')]).include[0];
  assert.equal(row.version, '1.20.1-2.4.0-beta.1');
  assert.equal(row.versionId, '1.20.1-2.4.0-beta.1-fabric');
  assert.equal(row.versionType, 'beta');
});

test('duplicate loader identities and unsupported Minecraft-loader pairs fail', () => {
  assert.throws(() => planRelease(release, [asset('1.21.1'), loaderAsset('1.21.1', 'neoforge')]), /Multiple/);
  assert.throws(() => planRelease(release, [loaderAsset('1.20.1', 'neoforge')]), /Unsupported loader/);
  assert.throws(() => planRelease(release, [loaderAsset('1.21.1', 'forge')]), /Unsupported loader/);
  assert.throws(() => planRelease(release, [], { loader: 'quilt' }), /Unsupported loader/);
});

test('single NeoForge release only creates NeoForge destinations', () => {
  const rows = planRelease(release, [asset('1.21.1')]).include;
  assert.equal(rows.length, 2);
  assert.ok(rows.every(r => r.minecraft === '1.21.1' && r.loader === 'neoforge' && r.java === '21'));
});
test('single Forge release only creates Forge destinations', () => {
  const rows = planRelease(release, [asset('1.20.1')]).include;
  assert.equal(rows.length, 2);
  assert.ok(rows.every(r => r.loader === 'forge' && r.java === '17'));
});
test('dual release produces four independent jobs and distinct version identifiers', () => {
  const rows = planRelease(release, [asset('1.20.1'), asset('1.21.1')]).include;
  assert.equal(rows.length, 4);
  assert.equal(new Set(rows.map(r => r.version)).size, 2);
  assert.deepEqual(rows.map(r => r.platform), ['curseforge', 'modrinth', 'curseforge', 'modrinth']);
});
test('branch versions come from each asset, including different mod versions', () => {
  const rows = planRelease(release, [asset('1.20.1'), asset('1.21.1', '2.2.1')]).include;
  assert.equal(rows[2].version, '1.21.1-2.2.1');
});
test('manual retry selects exactly one branch and platform', () => {
  const rows = planRelease(release, [asset('1.20.1'), asset('1.21.1')],
    { branch: '1.20.1', platform: 'modrinth' }).include;
  assert.equal(rows.length, 1);
  assert.equal(rows[0].platform, 'modrinth');
  assert.equal(rows[0].minecraft, '1.20.1');
});
test('prereleases map to beta', () => {
  assert.equal(planRelease({ ...release, prerelease: true }, [asset('1.21.1', '2.3.0-beta.1')]).include[0].versionType, 'beta');
});
test('source and documentation artifacts never become production uploads', () => {
  assert.equal(planRelease(release, [asset('1.21.1'), { name: 'tradetweaks-1.21.1-2.2.0-sources.jar' },
    { name: 'changelog.md' }]).include.length, 2);
});
test('empty, missing and incomplete selections fail before uploads', () => {
  assert.throws(() => planRelease(release, []), /No matching/);
  assert.throws(() => planRelease(release, [asset('1.21.1')], { branch: '1.20.1' }), /No matching/);
  assert.throws(() => planRelease(release, [{ ...asset('1.21.1'), state: 'new' }]), /Incomplete/);
});
test('ambiguous and foreign production JARs fail before uploads', () => {
  assert.throws(() => planRelease(release, [asset('1.20.1'), asset('1.20.1', '2.2.1')]), /Multiple/);
  assert.throws(() => planRelease(release, [{ name: '../bad.jar' }]), /Unrecognized/);
  assert.throws(() => planRelease(release, [asset('1.22.1')]), /Unrecognized/);
});
test('draft, empty changelog and invalid filters fail', () => {
  assert.throws(() => planRelease({ ...release, draft: true }, []), /Publish/);
  assert.throws(() => planRelease({ ...release, body: '' }, []), /changelog/);
  assert.throws(() => planRelease(release, [], { platform: 'unknown' }), /Unsupported/);
  assert.throws(() => planRelease(release, [], { branch: 'main' }), /Unsupported/);
});

/** Stub the GitHub boundary and filesystem while exercising the production snapshot code. */
function snapshotFixture(t, overrides = {}) {
  const bytes = Buffer.from('release-file-bytes');
  const file = { ...asset('1.21.1'), size: bytes.length,
    digest: `sha256:${crypto.createHash('sha256').update(bytes).digest('hex')}`, ...overrides };
  const written = new Map();
  t.mock.method(fs, 'mkdirSync', () => {});
  t.mock.method(fs, 'writeFileSync', (name, data) => written.set(name, data));
  const download = t.mock.fn(async () => ({ data: bytes.buffer.slice(bytes.byteOffset, bytes.byteOffset + bytes.byteLength) }));
  const getReleaseByTag = t.mock.fn(async () => ({ data: release }));
  const core = { info() {}, setOutput: t.mock.fn(), summary: {
    addHeading() { return this; }, addTable() { return this; }, async write() {},
  } };
  return { written, download, getReleaseByTag, args: { core,
    context: { repo: { owner: 'LostPatrol', repo: 'TradeTweaks' }, eventName: 'release', payload: { release } },
    github: { paginate: async () => [file], rest: { repos: { getReleaseByTag,
      listReleaseAssets() {}, getReleaseAsset: download } } },
  } };
}
test('snapshot downloads each JAR once and preserves exact changelog and bytes', async t => {
  const f = snapshotFixture(t);
  await prepareRelease(f.args);
  assert.equal(f.download.mock.callCount(), 1);
  assert.equal(f.written.get('release-assets/changelog.md'), release.body);
  assert.equal(f.written.get('release-assets/tradetweaks-1.21.1-2.2.0.jar').toString(), 'release-file-bytes');
  assert.equal(JSON.parse(f.written.get('release-assets/plan.json')).include.length, 2);
});
test('snapshot rejects changed bytes before writing artifacts', async t => {
  const f = snapshotFixture(t, { digest: 'sha256:incorrect' });
  await assert.rejects(prepareRelease(f.args), /digest mismatch/);
  assert.equal(f.written.size, 0);
});
test('snapshot rejects truncated downloads', async t => {
  const f = snapshotFixture(t, { size: 999 });
  await assert.rejects(prepareRelease(f.args), /size mismatch/);
});
test('manual dispatch retrieves the specified historical Release', async t => {
  const f = snapshotFixture(t);
  const previous = process.env.RELEASE_TAG;
  process.env.RELEASE_TAG = '2.2.0';
  f.args.context.eventName = 'workflow_dispatch';
  try {
    await prepareRelease(f.args);
    assert.equal(f.getReleaseByTag.mock.calls[0].arguments[0].tag, '2.2.0');
  } finally {
    if (previous === undefined) delete process.env.RELEASE_TAG;
    else process.env.RELEASE_TAG = previous;
  }
});
