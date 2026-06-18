$ErrorActionPreference = 'Stop'
$bad = Get-ChildItem src -Recurse -File -Include *.java,*.yml,*.yaml | Select-String -Pattern "`t| +$"
if ($bad) { $bad | ForEach-Object { Write-Error "$($_.Path):$($_.LineNumber) invalid whitespace" } }
mvn -q -DskipTests compile
