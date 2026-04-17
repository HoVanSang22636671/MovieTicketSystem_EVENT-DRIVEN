$ErrorActionPreference = 'Stop'

function Fail($message) {
  Write-Error $message
  exit 1
}

function AssertNoMatchInFiles {
  param(
    [Parameter(Mandatory=$true)][string[]]$Files,
    [Parameter(Mandatory=$true)][string]$Pattern,
    [Parameter(Mandatory=$true)][string]$Message,
    [switch]$Simple
  )

  if ($Files.Count -eq 0) {
    return
  }

  $match = if ($Simple) {
    Select-String -Path $Files -SimpleMatch -Pattern $Pattern -List -ErrorAction SilentlyContinue
  } else {
    Select-String -Path $Files -Pattern $Pattern -List -ErrorAction SilentlyContinue
  }

  if ($null -ne $match) {
    $location = "{0}:{1}" -f $match.Path, $match.LineNumber
    Fail ("{0}`nFound: {1}`nLine: {2}`nAt: {3}" -f $Message, $match.Line.Trim(), $match.LineNumber, $match.Path)
  }
}

function AssertContains {
  param(
    [Parameter(Mandatory=$true)][string]$File,
    [Parameter(Mandatory=$true)][string]$Pattern,
    [Parameter(Mandatory=$true)][string]$Message
  )

  if (-not (Test-Path $File)) {
    Fail "Missing required file: $File"
  }

  $match = Select-String -Path $File -Pattern $Pattern -List -ErrorAction SilentlyContinue
  if ($null -eq $match) {
    Fail $Message
  }
}

$repoRoot = Split-Path -Parent $PSScriptRoot
Set-Location $repoRoot

Write-Host "Running EDA guard checks..." -ForegroundColor Cyan

# 1) Disallow REST client frameworks in code (they encourage sync service-to-service calls)
$javaFiles = Get-ChildItem -Path . -Recurse -File -Filter *.java |
  Where-Object { $_.FullName -notmatch "\\target\\" -and $_.FullName -notmatch "\\node_modules\\" } |
  ForEach-Object { $_.FullName }

$restClientBans = @(
  'RestTemplate',
  'WebClient',
  'FeignClient',
  '@FeignClient'
)

foreach ($ban in $restClientBans) {
  AssertNoMatchInFiles -Files $javaFiles -Pattern $ban -Message "EDA violation: REST client '$ban' found in Java sources. Booking/payment flow must be async via RabbitMQ events." -Simple
}

# 2) Specifically disallow hard-coded http(s) calls inside booking/payment services (source only)
$edaSensitiveJava = Get-ChildItem -Path .\booking-service\src\main\java, .\payment-service\src\main\java -Recurse -File -Filter *.java -ErrorAction SilentlyContinue |
  ForEach-Object { $_.FullName }

AssertNoMatchInFiles -Files $edaSensitiveJava -Pattern 'http://|https://' -Message "EDA violation: Hard-coded http(s) URL found in booking-service/payment-service source. Use RabbitMQ events, not synchronous HTTP." 

# 3) Gateway must not expose any payment HTTP route
$gatewayConfig = Join-Path $repoRoot 'gateway\src\main\resources\application.yml'
AssertNoMatchInFiles -Files @($gatewayConfig) -Pattern ':8084' -Message "EDA violation: gateway config references port 8084 (payment). Payment is a worker (no public HTTP API)." -Simple
AssertNoMatchInFiles -Files @($gatewayConfig) -Pattern '/api/payments' -Message "EDA violation: gateway exposes /api/payments. Payment must be async via events." -Simple
AssertNoMatchInFiles -Files @($gatewayConfig) -Pattern 'payment-service' -Message "EDA violation: gateway routes to payment-service. Payment must not be an HTTP dependency." -Simple

# 4) Workers must be non-web apps
$paymentAppYml = Join-Path $repoRoot 'payment-service\src\main\resources\application.yml'
$notificationAppYml = Join-Path $repoRoot 'notification-worker\src\main\resources\application.yml'
AssertContains -File $paymentAppYml -Pattern 'web-application-type:\s*none' -Message "EDA violation: payment-service must set spring.main.web-application-type: none"
AssertContains -File $notificationAppYml -Pattern 'web-application-type:\s*none' -Message "EDA violation: notification-worker must set spring.main.web-application-type: none"

# 5) Compose should not publish worker ports
$composeFull = Join-Path $repoRoot 'infra\docker-compose.yml'
AssertNoMatchInFiles -Files @($composeFull) -Pattern '"8084:8084"' -Message "EDA violation: infra/docker-compose.yml publishes payment-service port 8084. It should be a worker (no public HTTP API)." -Simple
AssertNoMatchInFiles -Files @($composeFull) -Pattern '"8086:8086"' -Message "EDA violation: infra/docker-compose.yml publishes notification-worker port 8086. It should be a worker (no public HTTP API)." -Simple

Write-Host "EDA checks passed (OK)" -ForegroundColor Green
