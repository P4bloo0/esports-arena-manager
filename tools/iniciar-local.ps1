param([switch]$Demo, [string]$JavaHome = $env:JAVA_HOME)
$ErrorActionPreference='Stop'
$arenaRoot=Split-Path $PSScriptRoot
if (-not (Test-Path -LiteralPath (Join-Path $arenaRoot '.env'))) { & (Join-Path $PSScriptRoot 'configurar.ps1') }
Get-Content -LiteralPath (Join-Path $arenaRoot '.env') | ForEach-Object { if ($_ -match '^([A-Z_]+)=(.*)$') { [Environment]::SetEnvironmentVariable($Matches[1],$Matches[2],'Process') } }
$arenaJava=if($JavaHome) { Join-Path $JavaHome 'bin/java.exe' } else { (Get-Command java -ErrorAction Stop).Source }
$arenaServices=[ordered]@{'eureka-service'=8761;'gateway-service'=8080;'user-service'=8001;'game-service'=8002;'team-service'=8003;'tournament-service'=8004;'registration-service'=8005;'match-service'=8006;'result-service'=8007;'ranking-service'=8008;'sanction-service'=8009;'notification-service'=8010;'auth-service'=8011}
$arenaWork=Join-Path $arenaRoot 'work'
$arenaLogs=Join-Path $arenaWork 'logs'
New-Item -ItemType Directory -Force -Path $arenaLogs | Out-Null
$arenaPids=Join-Path $arenaWork 'local-pids.json'
if(Test-Path -LiteralPath $arenaPids) { throw 'Ya existe un registro de procesos. Ejecuta tools/detener-local.ps1 antes de iniciar otra vez.' }
foreach($arenaService in $arenaServices.Keys) {
 $arenaJar=Join-Path $arenaRoot "$arenaService/target/$arenaService-0.0.1-SNAPSHOT.jar"
 if(-not(Test-Path -LiteralPath $arenaJar)) { throw "Falta compilar $arenaService. Ejecuta mvnw.cmd verify desde la raiz." }
 if(Get-NetTCPConnection -LocalPort $arenaServices[$arenaService] -State Listen -ErrorAction SilentlyContinue) { throw "El puerto $($arenaServices[$arenaService]) ya esta ocupado." }
}
$arenaStarted=@()
try {
 foreach($arenaService in $arenaServices.Keys) {
  $arenaJar=Join-Path $arenaRoot "$arenaService/target/$arenaService-0.0.1-SNAPSHOT.jar"
  $arenaArgs=@('-Xms32m','-Xmx192m','-Dfile.encoding=UTF-8','-Deureka.client.registry-fetch-interval-seconds=2','-Deureka.instance.lease-renewal-interval-in-seconds=2','-Dspring.cloud.loadbalancer.cache.enabled=false')
  if($Demo -and $arenaService -notin @('eureka-service','gateway-service')) { $arenaDb=$arenaService.Replace('-','_'); $arenaArgs+=@("-Dspring.datasource.url=jdbc:h2:mem:$arenaDb;MODE=MySQL;DB_CLOSE_DELAY=-1",'-Dspring.datasource.driver-class-name=org.h2.Driver','-Dspring.datasource.username=sa','-Dspring.datasource.password=','-Dspring.jpa.hibernate.ddl-auto=create-drop') }
  $arenaArgs+=@('-jar',('"'+$arenaJar+'"'))
  $arenaProcess=Start-Process -FilePath $arenaJava -ArgumentList $arenaArgs -WorkingDirectory $arenaRoot -RedirectStandardOutput (Join-Path $arenaLogs "$arenaService.out.log") -RedirectStandardError (Join-Path $arenaLogs "$arenaService.err.log") -WindowStyle Hidden -PassThru
  $arenaStarted+=@{Id=$arenaProcess.Id;Servicio=$arenaService;Jar=$arenaJar}
  $arenaStarted | ConvertTo-Json | Set-Content -LiteralPath $arenaPids -Encoding utf8
 }
 Write-Output 'Servicios iniciados en segundo plano. El arranque y registro en Eureka puede tardar unos minutos.'
 Write-Output "Registros: $arenaLogs"
 if($Demo) { Write-Output 'Modo Demo: bases H2 nuevas en memoria; sus datos desaparecen al detener los servicios.' }
} catch { & (Join-Path $PSScriptRoot 'detener-local.ps1'); throw }
