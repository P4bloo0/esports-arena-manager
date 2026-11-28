param([Parameter(Mandatory)][string]$Nickname,[Parameter(Mandatory)][string]$Nombre,[Parameter(Mandatory)][string]$Email,[string]$AuthUrl='http://localhost:8011')
$ErrorActionPreference='Stop'
$arenaRoot=Split-Path $PSScriptRoot
$arenaAuthUri=[Uri]$AuthUrl
$arenaClaveInterna=$env:ARENA_INTERNAL_SECRET
if ($arenaAuthUri.IsLoopback) {
    $arenaEntorno=Join-Path $arenaRoot '.env'
    if (Test-Path -LiteralPath $arenaEntorno) {
        $arenaLinea=Get-Content -LiteralPath $arenaEntorno | Select-String '^ARENA_INTERNAL_SECRET=(.+)$' | Select-Object -First 1
        if ($arenaLinea) { $arenaClaveInterna=$arenaLinea.Matches[0].Groups[1].Value }
    }
} elseif ($arenaAuthUri.Scheme -ne 'https') {
    throw 'La URL remota del servicio de autenticacion debe usar HTTPS.'
}
if (-not $arenaClaveInterna) {
    $arenaSecreto=Read-Host 'Clave interna del backend' -AsSecureString
    $arenaClaveInterna=[Net.NetworkCredential]::new('', $arenaSecreto).Password
}
$arenaClave=Read-Host 'Contrasena del administrador (minimo 8 caracteres)' -AsSecureString
$arenaPassword=[Net.NetworkCredential]::new('', $arenaClave).Password
$arenaBody=@{nickname=$Nickname;nombre=$Nombre;email=$Email;password=$arenaPassword} | ConvertTo-Json
$null=Invoke-RestMethod -Method Post -Uri "$($AuthUrl.TrimEnd('/'))/internal/auth/bootstrap" -Headers @{'X-Arena-Internal'=$arenaClaveInterna} -ContentType 'application/json; charset=utf-8' -Body ([Text.Encoding]::UTF8.GetBytes($arenaBody))
Write-Output 'Administrador inicial creado. Puedes iniciar sesion mediante /api/v1/auth/login.'
