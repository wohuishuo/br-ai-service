$ErrorActionPreference = 'Stop'
& "$PSScriptRoot\lint.ps1"
mvn -B verify
