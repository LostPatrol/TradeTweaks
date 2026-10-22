# Validate embedded mod identity and version before distributing release JARs.
$ErrorActionPreference = 'Stop'
$plan = Get-Content 'release-assets/plan.json' -Raw | ConvertFrom-Json
foreach ($build in ($plan.include | Sort-Object name -Unique)) {
    $jar = [System.IO.Compression.ZipFile]::OpenRead((Join-Path (Get-Location) "release-assets/$($build.name)"))
    try {
        $metadataPath = switch ($build.loader) {
            'forge' { 'META-INF/mods.toml' }
            'neoforge' { 'META-INF/neoforge.mods.toml' }
            'fabric' { 'fabric.mod.json' }
            default { throw "Unsupported loader: $($build.loader)" }
        }
        $entry = $jar.GetEntry($metadataPath)
        if (-not $entry) { throw "Missing $metadataPath in $($build.name)" }
        $reader = [System.IO.StreamReader]::new($entry.Open())
        try { $metadata = $reader.ReadToEnd() } finally { $reader.Dispose() }
        if ($build.loader -eq 'fabric') {
            $mod = $metadata | ConvertFrom-Json
            if ($mod.id -ne 'tradetweaks') { throw "Wrong mod ID: $($build.name)" }
            if (-not $mod.depends.fabricloader -or -not $mod.depends.minecraft) {
                throw "Missing Fabric/Minecraft dependency: $($build.name)"
            }
            $version = $mod.version
        } else {
            # Restrict checks to this mod's declaration instead of dependency version ranges.
            $mod = [regex]::Match($metadata, '(?ms)^\s*\[\[mods\]\]\s*$(.*?)(?=^\s*\[|\z)').Groups[1].Value
            if ($mod -notmatch '(?m)^\s*modId\s*=\s*"tradetweaks"\s*$') { throw "Wrong mod ID: $($build.name)" }
            $version = [regex]::Match($mod, '(?m)^\s*version\s*=\s*"([^"]+)"').Groups[1].Value
        }
        if ($version -ne $build.version) { throw "JAR version '$version' does not match '$($build.version)'" }
        Write-Output "Validated $($build.name) ($($build.loader))"
    } finally { $jar.Dispose() }
}
