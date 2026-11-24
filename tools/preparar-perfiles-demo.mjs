import { randomBytes } from "node:crypto";
import { mkdir, readFile, writeFile } from "node:fs/promises";
import { homedir } from "node:os";
import { join } from "node:path";

const gateway = "http://localhost:8080/api/v1";
const autenticacion = "http://localhost:8011";
const directorio = join(process.env.LOCALAPPDATA ?? join(homedir(), ".local", "state"), "eSportsArena");
const archivoClaves = join(directorio, "perfiles-demo.json");
const perfiles = [
  { nickname: "admin", nombre: "Admin", rol: "ADMINISTRADOR" },
  { nickname: "leo", nombre: "Leo", rol: "JUGADOR" },
  { nickname: "camila", nombre: "Camila", rol: "JUGADOR" },
  { nickname: "felipe", nombre: "Felipe", rol: "JUGADOR" },
  { nickname: "martina", nombre: "Martina", rol: "JUGADOR" },
  { nickname: "andrea", nombre: "Andrea", rol: "ORGANIZADOR" },
];

async function leerClaves() {
  try {
    return JSON.parse(await readFile(archivoClaves, "utf8"));
  } catch (error) {
    if (error.code !== "ENOENT") throw error;
    const nuevas = Object.fromEntries(perfiles.map(({ nickname }) =>
      [nickname, randomBytes(12).toString("base64url")]));
    await mkdir(directorio, { recursive: true, mode: 0o700 });
    try {
      await writeFile(archivoClaves, JSON.stringify(nuevas, null, 2) + "\n", { mode: 0o600, flag: "wx" });
      return nuevas;
    } catch (fallo) {
      if (fallo.code !== "EEXIST") throw fallo;
      return JSON.parse(await readFile(archivoClaves, "utf8"));
    }
  }
}

async function solicitar(ruta, { origen = gateway, token, datos, cabeceras = {} } = {}) {
  const respuesta = await fetch(origen + ruta, {
    method: datos ? "POST" : "GET",
    headers: {
      ...(token && { Authorization: `Bearer ${token}` }),
      ...(datos && { "Content-Type": "application/json" }),
      ...cabeceras,
    },
    body: datos ? JSON.stringify(datos) : undefined,
    signal: AbortSignal.timeout(15000),
  });
  const cuerpo = await respuesta.json().catch(() => null);
  if (respuesta.ok) return cuerpo;
  const error = new Error(`${ruta} respondió ${respuesta.status}. ${cuerpo?.mensaje ?? ""}`.trim());
  error.estado = respuesta.status;
  throw error;
}

async function buscarSesion(nickname, password) {
  try {
    return await solicitar("/auth/login", { datos: { nickname, password } });
  } catch (error) {
    if (error.estado !== 401 && error.estado !== 409) throw error;
    return null;
  }
}

async function crearAdministrador(datos) {
  const token = process.env.ARENA_DEMO_TOKEN_ADMIN;
  if (token) return solicitar("/auth/admin/cuentas?rol=ADMINISTRADOR", { token, datos });

  const configuracion = await readFile(new URL("../.env", import.meta.url), "utf8");
  const clave = configuracion.match(/^ARENA_INTERNAL_SECRET=([^\r\n]+)/m)?.[1];
  if (!clave) throw new Error("Falta ARENA_INTERNAL_SECRET en el .env del backend.");
  return solicitar("/internal/auth/bootstrap", {
    origen: autenticacion,
    cabeceras: { "X-Arena-Internal": clave },
    datos,
  });
}

async function prepararCuentas(claves) {
  const cuentas = new Map();
  for (const { nickname, nombre, rol } of perfiles) {
    const password = claves[nickname];
    if (!password) throw new Error(`Falta la clave local de ${nickname}.`);
    const datos = { nickname, nombre, email: `${nickname}.arena@example.test`, password };
    let cuenta = await buscarSesion(nickname, password);
    if (!cuenta) {
      cuenta = rol === "ADMINISTRADOR"
        ? await crearAdministrador(datos)
        : await solicitar(`/auth/admin/cuentas?rol=${rol}`, { token: cuentas.get("admin").token, datos });
    }
    if (!cuenta.roles.includes(rol)) throw new Error(`El perfil ${nickname} tiene un rol distinto de ${rol}.`);
    cuentas.set(nickname, cuenta);
  }
  return cuentas;
}

async function prepararJuego(datos, token) {
  const juegos = await solicitar("/juegos");
  const existente = juegos.find((juego) => juego.nombre.toLowerCase() === datos.nombre.toLowerCase());
  return existente ?? solicitar("/juegos", { token, datos });
}

async function prepararEquipo(nombre, juego, capitan, integrante) {
  const equipos = await solicitar("/equipos");
  let equipo = equipos.find((actual) => actual.nombre === nombre);
  if (!equipo) {
    equipo = await solicitar("/equipos", {
      token: capitan.token,
      datos: { nombre, juegoPrincipalId: juego.juegoId, capitanId: capitan.userId },
    });
  } else if (equipo.capitanId !== capitan.userId || equipo.juegoPrincipalId !== juego.juegoId) {
    throw new Error(`Ya existe un equipo distinto con el nombre ${nombre}.`);
  }

  const integrantes = await solicitar(`/equipos/${equipo.equipoId}/miembros`);
  if (!integrantes.some((miembro) => miembro.usuarioId === integrante.userId)) {
    await solicitar("/equipos/miembros", {
      token: capitan.token,
      datos: { equipoId: equipo.equipoId, usuarioId: integrante.userId, rolDentroEquipo: "TITULAR" },
    });
  }
}

try {
  const cuentas = await prepararCuentas(await leerClaves());
  const { token } = cuentas.get("admin");
  const minecraft = await prepararJuego({
    nombre: "Minecraft", genero: "Sandbox", modalidad: "2v2", jugadoresPorEquipo: 2,
  }, token);
  const lol = await prepararJuego({
    nombre: "League of Legends", genero: "MOBA", modalidad: "5v5", jugadoresPorEquipo: 5,
  }, token);
  await prepararEquipo("Creepers Arena", minecraft, cuentas.get("felipe"), cuentas.get("leo"));
  await prepararEquipo("Nexo Arena", lol, cuentas.get("martina"), cuentas.get("camila"));
  console.log("Perfiles y equipos de demostración preparados.");
  console.log(`Claves locales en ${archivoClaves}`);
} catch (error) {
  console.error(error.message);
  process.exitCode = 1;
}
