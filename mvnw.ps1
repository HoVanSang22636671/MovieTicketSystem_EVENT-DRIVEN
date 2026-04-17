$ErrorActionPreference = 'Stop'

$projectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$mavenVersion = '3.9.9'
$mavenBaseDir = Join-Path $projectDir '.mvn'
$mavenDir = Join-Path $mavenBaseDir ("apache-maven-$mavenVersion")
$mvnCmd = Join-Path $mavenDir 'bin\mvn.cmd'

if (-not (Test-Path $mvnCmd)) {
  New-Item -ItemType Directory -Force -Path $mavenBaseDir | Out-Null

  $zipPath = Join-Path $mavenBaseDir ("apache-maven-$mavenVersion-bin.zip")
  $downloadUrl = "https://archive.apache.org/dist/maven/maven-3/$mavenVersion/binaries/apache-maven-$mavenVersion-bin.zip"

  Write-Host "Downloading Maven $mavenVersion..." -ForegroundColor Cyan
  Invoke-WebRequest -Uri $downloadUrl -OutFile $zipPath

  Write-Host "Extracting Maven..." -ForegroundColor Cyan
  if (Test-Path $mavenDir) {
    Remove-Item -Recurse -Force $mavenDir
  }
  Expand-Archive -Path $zipPath -DestinationPath $mavenBaseDir -Force
}

& $mvnCmd @args
exit $LASTEXITCODE
