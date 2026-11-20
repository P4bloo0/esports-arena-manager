param([Parameter(Mandatory)][string]$Nickname,[Parameter(Mandatory)][string]$Nombre,[Parameter(Mandatory)][string]$Email,[string]$AuthUrl='http://localhost:8011')
$ErrorActionPreference='Stop'
$arenaRoot=Split-Path $PSScriptRoot
Get-Content -LiteralPath (Join-Path $arenaRoot '.env') | ForEach-Object { if($_ -match '^([A-Z_]+)=(.*)$') { [Environment]::SetEnvironmentVariable($Matches[1],$Matches[2],'Process') } }
$arenaClave=Read-Host 'Contrasena del administrador (minimo 8 caracteres)' -AsSecureString
$arenaPassword=[Net.NetworkCredential]::new('', $arenaClave).Password
$arenaBody=@{nickname=$Nickname;nombre=$Nombre;email=$Email;password=$arenaPassword} | ConvertTo-Json
$null=Invoke-RestMethod -Method Post -Uri "$AuthUrl/internal/auth/bootstrap" -Headers @{'X-Arena-Internal'=$env:ARENA_INTERNAL_SECRET} -ContentType 'application/json; charset=utf-8' -Body ([Text.Encoding]::UTF8.GetBytes($arenaBody))
Write-Output 'Administrador inicial creado. Puedes iniciar sesion mediante /api/v1/auth/login.'
