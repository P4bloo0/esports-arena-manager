"""Prueba de integracion HTTP. Ejecutar con los 13 servicios en Demo y sin administrador previo."""
import json, os, time, secrets, urllib.request, urllib.error
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
env=dict(line.split('=',1) for line in (ROOT/'.env').read_text(encoding='utf-8-sig').splitlines() if line and not line.startswith('#'))
BASE='http://localhost:8080'
checks=[]
def call(path,method='GET',data=None,token=None,status=200,base=BASE,headers=None):
    h={'Content-Type':'application/json',**(headers or {})}
    if token: h['Authorization']='Bearer '+token
    body=None if data is None else json.dumps(data).encode()
    request=urllib.request.Request(base+path,data=body,headers=h,method=method)
    try:
        with urllib.request.urlopen(request,timeout=45) as response: code=response.status; raw=response.read()
    except urllib.error.HTTPError as error: code=error.code; raw=error.read()
    result=json.loads(raw) if raw else None
    if code!=status: raise AssertionError(f'{method} {path}: esperaba {status}, recibio {code}: {result}')
    return result
def check(name,condition=True):
    if not condition: raise AssertionError(name)
    checks.append(name); print('OK:',name,flush=True)
def wait_ready():
    for _ in range(120):
        try:
            for port in range(8001,8012): call('/v3/api-docs',base=f'http://localhost:{port}')
            call('/api/v1/torneos'); return
        except (OSError,AssertionError): time.sleep(2)
    raise RuntimeError('Los servicios no estan disponibles. Revisa work/logs.')
def run():
    wait_ready(); check('Los servicios responden y el Gateway descubre torneos')
    request=urllib.request.Request(BASE+'/api/v1/auth/login',method='OPTIONS',headers={'Origin':'http://localhost:5173','Access-Control-Request-Method':'POST','Access-Control-Request-Headers':'authorization,content-type'})
    with urllib.request.urlopen(request) as response: check('CORS permite el origen del frontend',response.headers.get('Access-Control-Allow-Origin')=='http://localhost:5173')
    tag=secrets.token_hex(3); password=secrets.token_hex(12)
    def account(n): return dict(nickname=n+tag,nombre='Prueba '+n,email=n+tag+'@example.test',password=password)
    admin=call('/internal/auth/bootstrap','POST',account('admin'),status=200,base='http://localhost:8011',headers={'X-Arena-Internal':env['ARENA_INTERNAL_SECRET']})
    at=admin['token']
    org=call('/api/v1/auth/admin/cuentas?rol=ORGANIZADOR','POST',account('org'),at)
    ot=org['token']; players=[call('/api/v1/auth/register','POST',account('p'+str(i)),status=201) for i in range(4)]
    check('Registro vincula la cuenta con un perfil real',call('/api/v1/usuarios/me',token=players[0]['token'])['usuarioId']==players[0]['userId'])
    call('/api/v1/auth/register','POST',{**account('escalada'),'roles':['ADMINISTRADOR']},status=400); check('El registro publico bloquea la escalada de rol')
    game=call('/api/v1/juegos','POST',dict(nombre='Duos '+tag,genero='Competitivo',modalidad='2v2',jugadoresPorEquipo=2),at,status=201)
    call('/api/v1/juegos','POST',dict(nombre='Prohibido',genero='FPS',modalidad='1v1',jugadoresPorEquipo=1),players[0]['token'],status=403); check('Jugador recibe 403 al administrar juegos')
    def team(player,name): return call('/api/v1/equipos','POST',dict(nombre=name+tag,capitanId=player['userId'],juegoPrincipalId=game['juegoId']),player['token'],status=201)
    ta=team(players[0],'EquipoA'); tb=team(players[2],'EquipoB')
    import datetime
    now=datetime.datetime.now(); iso=lambda d:(now+datetime.timedelta(days=d)).isoformat(timespec='seconds')
    tournament=call('/api/v1/torneos','POST',dict(nombre='Torneo '+tag,juegoId=game['juegoId'],fechaCierreInscripcion=iso(1),fechaInicio=iso(2),fechaFin=iso(3),cupoMaximo=2,modalidad='Duos'),at,status=201)
    tid=tournament['torneoId']; call(f'/api/v1/torneos/{tid}/premios','POST',dict(posicion=1,descripcion='Copa de prueba'),at)
    check('Los premios no se publican antes del cierre',call(f'/api/v1/torneos/{tid}/premios')==[])
    call(f'/api/v1/torneos/{tid}/estado?nuevo=ABIERTO','PUT',token=at)
    registration=lambda team:dict(torneoId=tid,equipoId=team['equipoId'],tipoParticipante='EQUIPO')
    call('/api/v1/inscripciones','POST',registration(ta),players[0]['token'],status=400); check('Equipo incompleto no puede inscribirse')
    for captain,member,team_ in [(players[0],players[1],ta),(players[2],players[3],tb)]: call('/api/v1/equipos/miembros','POST',dict(equipoId=team_['equipoId'],usuarioId=member['userId'],rolDentroEquipo='TITULAR'),captain['token'],status=201)
    call(f"/api/v1/equipos/{ta['equipoId']}/miembros/{players[1]['userId']}",'DELETE',token=players[2]['token'],status=403); check('Otro jugador no puede administrar el equipo')
    inscriptions=[call('/api/v1/inscripciones','POST',registration(t),p['token'],status=201) for t,p in [(ta,players[0]),(tb,players[2])]]
    check('Inscripciones pendientes reservan los dos cupos',all(i['estado']=='PENDIENTE' for i in inscriptions) and call(f'/api/v1/torneos/{tid}')['cupoActual']==2)
    for i in inscriptions: call(f"/api/v1/inscripciones/{i['inscripcionId']}/estado?nuevoEstado=CONFIRMADA",'PUT',token=ot)
    call(f'/api/v1/torneos/{tid}/estado?nuevo=EN_CURSO','PUT',token=at)
    match=call('/api/v1/partidas','POST',dict(torneoId=tid,participanteAId=ta['equipoId'],participanteBId=tb['equipoId'],ronda='FINAL',fechaHora=iso(2)),ot,status=201)
    mid=match['partidaId']; call(f'/api/v1/partidas/{mid}/estado?nuevo=EN_CURSO','PATCH',token=ot)
    result=call('/api/v1/resultados','POST',dict(partidaId=mid,ganadorId=tb['equipoId'],puntajeA=1,puntajeB=3),ot,status=201)
    call(f'/api/v1/partidas/{mid}/estado?nuevo=FINALIZADA','PATCH',token=ot)
    call(f"/api/v1/resultados/{result['resultadoId']}/validar",'PATCH',token=ot)
    ranking=call(f'/api/v1/rankings/torneo/{tid}'); check('Validar actualiza el ranking y calcula bien la victoria de B',len(ranking)==2 and ranking[0]['participanteId']==tb['equipoId'] and ranking[0]['puntos']==3 and ranking[0]['diferencia']==2)
    again=call(f'/api/v1/rankings/torneo/{tid}/recalcular','POST',token=ot); check('Recalcular no duplica los puntos',again==ranking)
    call(f'/api/v1/torneos/{tid}/estado?nuevo=FINALIZADO','PUT',token=at); awards=call(f'/api/v1/torneos/{tid}/premios'); check('Cerrar asigna y publica el premio',awards[0]['participanteId']==tb['equipoId'])
    notifications=[]
    for _ in range(20):
        notifications=call('/api/v1/notificaciones/me',token=players[2]['token'])
        if notifications: break
        time.sleep(1)
    check('El jugador ganador recibe la notificacion',len(notifications)==1 and notifications[0]['tipo']=='PREMIO')
    call(f'/api/v1/torneos/{tid}/estado?nuevo=FINALIZADO','PUT',token=at); time.sleep(1)
    check('Repetir el cierre no duplica la notificacion',len(call('/api/v1/notificaciones/me',token=players[2]['token']))==1)
    call(f"/api/v1/notificaciones/{notifications[0]['notificacionId']}",token=players[0]['token'],status=403); check('Notificaciones de otro jugador estan protegidas')
    report={'resultado':'OK','comprobaciones':checks,'torneoId':tid,'modo':'H2 + Eureka + Gateway + 11 servicios de negocio'}
    (ROOT/'work'/'smoke-result.json').write_text(json.dumps(report,ensure_ascii=False,indent=2),encoding='utf-8')
    print(f'{len(checks)} comprobaciones HTTP correctas. Detalle en work/smoke-result.json',flush=True)
if __name__=='__main__': run()
