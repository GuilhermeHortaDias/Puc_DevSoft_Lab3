param(
    [string]$PlantUmlJar = ''
)

$ErrorActionPreference = 'Stop'
$lab3Root = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$lab3Version = '1.2026.8'
$lab3ExpectedHash = 'b5ebc643668e36cb2cbe2c30b9aed8f88ef693840cd9f93eaf18d8660daaae63'

if (-not $PlantUmlJar) {
    $lab3Tools = Join-Path $lab3Root '.tools'
    New-Item -ItemType Directory -Force -Path $lab3Tools | Out-Null
    $PlantUmlJar = Join-Path $lab3Tools "plantuml-java8-$lab3Version.jar"
    if (-not (Test-Path -LiteralPath $PlantUmlJar)) {
        $lab3Url = "https://github.com/plantuml/plantuml/releases/download/v$lab3Version/plantuml-java8-$lab3Version.jar"
        Invoke-WebRequest -Uri $lab3Url -OutFile $PlantUmlJar
    }
}

$PlantUmlJar = (Resolve-Path -LiteralPath $PlantUmlJar).Path
$lab3ActualHash = (Get-FileHash -LiteralPath $PlantUmlJar -Algorithm SHA256).Hash.ToLowerInvariant()
if ($lab3ActualHash -ne $lab3ExpectedHash) {
    throw 'Checksum incorreto. O JAR nao sera executado. Use a distribuicao Java 8 da versao 1.2026.8.'
}

$lab3Java = (Get-Command java -ErrorAction Stop).Source
$lab3Dir = Join-Path $lab3Root 'docs/modelagem/diagramas'
$lab3Sources = @(Get-ChildItem -LiteralPath $lab3Dir -Filter '*.puml' | Sort-Object Name)
if ($lab3Sources.Count -ne 3) {
    throw 'Esperados os tres diagramas PlantUML da entrega.'
}
$lab3SourcePaths = @($lab3Sources | ForEach-Object { $_.FullName })

& $lab3Java '-Djava.awt.headless=true' -jar $PlantUmlJar -charset UTF-8 -checkonly @lab3SourcePaths
if ($LASTEXITCODE -ne 0) { throw 'Falha na verificacao de sintaxe PlantUML.' }

foreach ($lab3Format in @('-tsvg', '-tpng')) {
    & $lab3Java '-Djava.awt.headless=true' -jar $PlantUmlJar -charset UTF-8 -failfast2 $lab3Format @lab3SourcePaths
    if ($LASTEXITCODE -ne 0) { throw "Falha na renderizacao $lab3Format." }
}

Write-Output 'Tres diagramas exportados em SVG e PNG.'
