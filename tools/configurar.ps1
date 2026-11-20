param([string]$CorsOrigins = 'http://localhost:5173')
$ErrorActionPreference = 'Stop'
$arenaRoot = Split-Path $PSScriptRoot
$arenaEnvPath = Join-Path $arenaRoot '.env'
if (Test-Path -LiteralPath $arenaEnvPath) { Write-Output '.env ya existe; se conserva su configuracion.'; exit 0 }
function New-ArenaSecret {
 $arenaBytes=New-Object byte[] 32
 $arenaRandom=[Security.Cryptography.RandomNumberGenerator]::Create()
 try { $arenaRandom.GetBytes($arenaBytes) } finally { $arenaRandom.Dispose() }
 return ([BitConverter]::ToString($arenaBytes)).Replace('-','')
}
@("JWT_SECRET=$(New-ArenaSecret)","ARENA_INTERNAL_SECRET=$(New-ArenaSecret)","DB_PASSWORD=$(New-ArenaSecret)","CORS_ORIGINS=$CorsOrigins") | Set-Content -LiteralPath $arenaEnvPath -Encoding utf8
Write-Output 'Configuracion creada en .env. Las claves no se muestran y Git las ignora.'
