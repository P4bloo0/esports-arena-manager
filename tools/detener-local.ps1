$ErrorActionPreference='Stop'
$arenaRoot=Split-Path $PSScriptRoot
$arenaPids=Join-Path $arenaRoot 'work/local-pids.json'
if(-not(Test-Path -LiteralPath $arenaPids)) { Write-Output 'No hay procesos registrados.'; exit 0 }
foreach($arenaItem in @(Get-Content -LiteralPath $arenaPids -Raw | ConvertFrom-Json)) {
 $arenaProcess=Get-CimInstance Win32_Process -Filter "ProcessId=$($arenaItem.Id)" -ErrorAction SilentlyContinue
 if($arenaProcess -and $arenaProcess.CommandLine.Contains($arenaItem.Jar)) { Stop-Process -Id $arenaItem.Id -ErrorAction SilentlyContinue }
}
Remove-Item -LiteralPath $arenaPids
Write-Output 'Servicios locales detenidos.'
