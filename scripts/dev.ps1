param(
    [ValidateSet('ambiente', 'backend', 'frontend', 'verificar')]
    [string]$Acao = 'ambiente',
    [string]$JavaHome
)
$ErrorActionPreference = 'Stop'
$lab3Root = Split-Path -Parent $PSScriptRoot

if (-not $JavaHome) {
    $lab3Compiler = Get-Command javac -ErrorAction SilentlyContinue
    if ($lab3Compiler) { $JavaHome = Split-Path -Parent (Split-Path -Parent $lab3Compiler.Source) }
    elseif ($env:JAVA_HOME) { $JavaHome = $env:JAVA_HOME }
}
if (-not $JavaHome -or -not (Test-Path (Join-Path $JavaHome 'bin/java.exe'))) {
    throw 'Instale o JDK 21 ou informe -JavaHome com o caminho da instalação.'
}
$lab3JavaVersion = (& (Join-Path $JavaHome 'bin/java.exe') -version 2>&1 | Out-String)
if ($lab3JavaVersion -notmatch 'version "21\.') { throw 'Este projeto usa JDK 21. Informe -JavaHome com essa versão.' }
$env:JAVA_HOME = $JavaHome
$env:PATH = (Join-Path $JavaHome 'bin') + ';' + $env:PATH

$lab3EnvFile = Join-Path $lab3Root '.env'
if (Test-Path $lab3EnvFile) {
    foreach ($lab3Line in Get-Content -LiteralPath $lab3EnvFile) {
        if ($lab3Line -match '^(DB_PASSWORD|DB_USER|DB_URL)=(.*)$') {
            [Environment]::SetEnvironmentVariable($Matches[1], $Matches[2], 'Process')
        }
    }
}

function Invoke-Lab3Command([string]$Directory, [string]$Executable, [string[]]$Arguments) {
    Push-Location $Directory
    try {
        & $Executable @Arguments
        if ($LASTEXITCODE -ne 0) { throw "$Executable terminou com código $LASTEXITCODE." }
    } finally { Pop-Location }
}

switch ($Acao) {
    'ambiente' {
        Write-Host "JDK 21: $JavaHome"
        Invoke-Lab3Command $lab3Root 'node.exe' @('--version')
        Invoke-Lab3Command $lab3Root 'docker.exe' @('info', '--format', '{{.ServerVersion}}')
        Invoke-Lab3Command $lab3Root 'docker.exe' @('compose', 'up', '-d', '--wait')
        Invoke-Lab3Command (Join-Path $lab3Root 'frontend') 'npm.cmd' @('ci')
        Write-Host 'Ambiente pronto. Inicie backend e frontend em terminais separados.'
    }
    'backend' { Invoke-Lab3Command (Join-Path $lab3Root 'backend') '.\mvnw.cmd' @('spring-boot:run') }
    'frontend' { Invoke-Lab3Command (Join-Path $lab3Root 'frontend') 'npm.cmd' @('run', 'dev') }
    'verificar' {
        Invoke-Lab3Command (Join-Path $lab3Root 'backend') '.\mvnw.cmd' @('-B', 'verify')
        Invoke-Lab3Command (Join-Path $lab3Root 'frontend') 'npm.cmd' @('run', 'lint')
        Invoke-Lab3Command (Join-Path $lab3Root 'frontend') 'npm.cmd' @('run', 'format:check')
        Invoke-Lab3Command (Join-Path $lab3Root 'frontend') 'npm.cmd' @('run', 'build')
    }
}
