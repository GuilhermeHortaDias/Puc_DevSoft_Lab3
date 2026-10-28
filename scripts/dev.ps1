param(
    [ValidateSet('ambiente', 'backend', 'frontend', 'compilar', 'verificar')]
    [string]$Acao = 'ambiente',
    [string]$JavaHome,
    [string]$ArquivoInstitucional
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
    foreach ($lab3Line in Get-Content -LiteralPath $lab3EnvFile -Encoding UTF8) {
        if ($lab3Line -match '^\s*(DB_PASSWORD|DB_USER|DB_URL|DB_PORT|SMTP_HOST|SMTP_PORT|SMTP_USER|SMTP_PASSWORD|SMTP_AUTH|SMTP_STARTTLS|SMTP_FROM|MAILPIT_SMTP_PORT|MAILPIT_HTTP_PORT|INSTITUCIONAL_ARQUIVO|SERVER_PORT|VITE_API_TARGET)=(.*)$') {
            $lab3VariableName = $Matches[1]
            $lab3VariableValue = $Matches[2].Trim()
            if ($lab3VariableValue -match '^"(.*)"$' -or $lab3VariableValue -match "^'(.*)'$") {
                $lab3VariableValue = $Matches[1]
            }
            [Environment]::SetEnvironmentVariable($lab3VariableName, $lab3VariableValue, 'Process')
        }
    }
}

function Set-Lab3Institucional([bool]$Preparar) {
    $lab3SelectedFile = $ArquivoInstitucional
    if (-not $lab3SelectedFile) { $lab3SelectedFile = $env:INSTITUCIONAL_ARQUIVO }
    $lab3DefaultFile = -not $lab3SelectedFile
    if ($lab3DefaultFile) { $lab3SelectedFile = Join-Path $lab3Root '.local/institucional.json' }
    elseif (-not [IO.Path]::IsPathRooted($lab3SelectedFile)) {
        $lab3SelectedFile = Join-Path $lab3Root $lab3SelectedFile
    }
    $lab3SelectedFile = [IO.Path]::GetFullPath($lab3SelectedFile)
    if ($Preparar -and $lab3DefaultFile -and -not (Test-Path -LiteralPath $lab3SelectedFile)) {
        [IO.Directory]::CreateDirectory((Split-Path -Parent $lab3SelectedFile)) | Out-Null
        $lab3TemplateFile = Join-Path $lab3Root 'backend/config/institucional.exemplo.json'
        $lab3InstitutionalData = Get-Content -LiteralPath $lab3TemplateFile -Raw -Encoding UTF8 | ConvertFrom-Json
        $lab3InstitutionalData.professores[0].senha = [Guid]::NewGuid().ToString('N')
        $lab3InstitutionalJson = $lab3InstitutionalData | ConvertTo-Json -Depth 4
        [IO.File]::WriteAllText($lab3SelectedFile, $lab3InstitutionalJson, [Text.UTF8Encoding]::new($false))
        Write-Host 'Cadastro local fictício criado. Login: professor.demo. Consulte a senha no arquivo local.'
    }
    if (-not (Test-Path -LiteralPath $lab3SelectedFile -PathType Leaf)) {
        throw "Arquivo institucional ausente: $lab3SelectedFile. Execute a ação ambiente ou informe -ArquivoInstitucional."
    }
    $env:INSTITUCIONAL_ARQUIVO = $lab3SelectedFile
    Write-Host "Arquivo institucional: $lab3SelectedFile"
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
        Set-Lab3Institucional $true
        Write-Host "JDK 21: $JavaHome"
        Invoke-Lab3Command $lab3Root 'node.exe' @('--version')
        Invoke-Lab3Command $lab3Root 'docker.exe' @('info', '--format', '{{.ServerVersion}}')
        Invoke-Lab3Command $lab3Root 'docker.exe' @('compose', 'up', '-d', '--wait')
        Invoke-Lab3Command (Join-Path $lab3Root 'frontend') 'npm.cmd' @('ci')
        $lab3MailpitPort = if ($env:MAILPIT_HTTP_PORT) { $env:MAILPIT_HTTP_PORT } else { '8025' }
        Write-Host "Ambiente pronto. Mailpit: http://127.0.0.1:$lab3MailpitPort. Inicie backend e frontend em terminais separados."
    }
    'backend' {
        Set-Lab3Institucional $false
        Invoke-Lab3Command (Join-Path $lab3Root 'backend') '.\mvnw.cmd' @('spring-boot:run')
    }
    'frontend' { Invoke-Lab3Command (Join-Path $lab3Root 'frontend') 'npm.cmd' @('run', 'dev') }
    'compilar' {
        Invoke-Lab3Command (Join-Path $lab3Root 'backend') '.\mvnw.cmd' @('-B', '-DskipTests', 'package')
        Invoke-Lab3Command (Join-Path $lab3Root 'frontend') 'npm.cmd' @('run', 'lint')
        Invoke-Lab3Command (Join-Path $lab3Root 'frontend') 'npm.cmd' @('run', 'format:check')
        Invoke-Lab3Command (Join-Path $lab3Root 'frontend') 'npm.cmd' @('run', 'build')
    }
    'verificar' {
        Invoke-Lab3Command (Join-Path $lab3Root 'backend') '.\mvnw.cmd' @('-B', 'verify')
        Invoke-Lab3Command (Join-Path $lab3Root 'frontend') 'npm.cmd' @('run', 'lint')
        Invoke-Lab3Command (Join-Path $lab3Root 'frontend') 'npm.cmd' @('run', 'format:check')
        Invoke-Lab3Command (Join-Path $lab3Root 'frontend') 'npm.cmd' @('run', 'build')
    }
}
