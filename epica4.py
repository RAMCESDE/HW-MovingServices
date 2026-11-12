"""Prueba rápida de flujo — Épica 4, en una sola corrida y con 1 segundo de pausa entre entradas."""

import sys
import time

import pexpect

CLASSES = sys.argv[1]
PAUSA = 1.5

app = pexpect.spawn(
    "java",
    ["-cp", CLASSES, "app.Application"],
    encoding="utf-8",
    timeout=20,
)
app.logfile_read = sys.stdout


def titulo(texto):
    print(f"\n\n######## {texto} ########", flush=True)


def responder(prompt, valor):
    app.expect_exact(prompt)
    time.sleep(PAUSA)
    app.sendline(valor)
    app.expect_exact(
        valor + "\r\n"
    )  # consume el eco para que los títulos salgan en orden


def menu(valor):
    responder("Seleccione:", valor)


def login(email, password):
    menu("2")
    responder("Correo electrónico:", email)
    responder("Contraseña:", password)


def capturar(patron):
    app.expect(patron)
    return app.match.group(1)


def empleado(
    nombre,
    documento,
    telefono,
    correo,
    cargo,
    estado_civil,
    eps,
    licencia,
    vence,
    talla,
    edad,
):
    menu("11")
    responder("Nombre:", nombre)
    responder("Documento:", documento)
    responder("Teléfono:", telefono)
    responder("Correo:", correo)
    responder("Contraseña temporal:", "Clave123*")
    menu(cargo)
    responder("Turno:", "Mañana")
    responder("Edad:", edad)
    responder("Sexo:", "M")
    responder("Estado civil:", estado_civil)
    responder("EPS:", eps)
    responder("(vacío si no aplica):", licencia)
    responder("vacío si no aplica):", vence)
    responder("Talla de dotación:", talla)
    return capturar(r"Empleado registrado con id (EMP-\w+)")


titulo("PREPARACIÓN — Administrador inicia sesión")
login("admin@hwmoving.com", "Admin123*")

titulo("Registrar vehículo (opción 1)")
menu("1")
responder("Placa:", "NPR100")
menu("3")
responder("Modelo:", "2022")
responder("(kg):", "4000")
responder("(m³):", "28")
responder("(AAAA-MM-DD):", "2027-06-01")
responder("(AAAA-MM-DD):", "2027-06-01")

titulo("Registrar conductor (opción 11)")
carlos = empleado(
    "Carlos Ruiz",
    "111222333",
    "3001112233",
    "carlos@hw.com",
    "1",
    "Soltero",
    "Sura",
    "C2",
    "2028-01-01",
    "L",
    "35",
)
titulo("Registrar auxiliar (opción 11)")
pedro = empleado(
    "Pedro Gómez",
    "444555666",
    "3004445566",
    "pedro@hw.com",
    "2",
    "Soltero",
    "Nueva EPS",
    "",
    "",
    "M",
    "28",
)
menu("0")

titulo("CLIENTE — Registro e inicio de sesión")
menu("1")
responder("Correo electrónico:", "ana@hw.com")
responder("Contraseña:", "Clave123*")
responder("(s/n):", "s")
login("ana@hw.com", "Clave123*")

titulo("Cotizar (opción 3)")
menu("3")
menu("2")
responder("(AAAA-MM-DD):", "2026-11-20")
menu("1")
responder("Dirección:", "Calle 10 # 20-30")
responder("Piso:", "2")
responder("(s/n):", "n")
menu("4")
responder("Dirección:", "Carrera 43 # 5-10")
responder("Piso:", "1")
responder("(s/n):", "s")
responder("(s/n):", "n")
responder("Volumen (m³):", "5")
menu("2")
responder("Ayudantes requeridos:", "1")
sol = capturar(r"Cotización (SOL-\w+)")
responder("4. Decidir después:", "1")

titulo("Reservar y pagar anticipo (opción 6)")
menu("6")
responder("Id de la cotización aceptada:", sol)
responder("(AAAA-MM-DD HH:MM):", "2026-11-20 09:00")
menu("2")
responder("(vacío = 10% mínimo):", "")
responder("tok_rechazado):", "tok_aprobado")
res = capturar(r"(RES-\w+)")
menu("0")

titulo("US-14 — Administrador programa el servicio (opción 15)")
login("admin@hwmoving.com", "Admin123*")
menu("15")
responder("Id de la reserva:", res)
responder("(AAAA-MM-DD HH:MM):", "2026-11-20 09:00")
responder("Placa:", "NPR100")
responder("Id del conductor:", carlos)
responder("Id del auxiliar 1:", pedro)

titulo("US-15 — Ver agenda (opción 16)")
menu("16")
responder("(AAAA-MM-DD):", "2026-11-20")
responder("(AAAA-MM-DD):", "2026-11-20")
menu("0")

titulo("US-16 — Conductor: ver mis servicios (opción 1)")
login("carlos@hw.com", "Clave123*")
menu("1")
for hito in ("En desplazamiento", "En origen", "En destino"):
    titulo(f"US-16 — Conductor: siguiente hito (opción 2) → {hito}")
    menu("2")
    responder("Id de la reserva:", res)
menu("0")

titulo("US-16 lado cliente — Ver estado (opción 8)")
login("ana@hw.com", "Clave123*")
menu("8")
responder("Id de la reserva:", res)

titulo("US-17 — Pagar saldo (opción 9)")
menu("9")
responder("Id de la reserva:", res)
menu("1")
responder("tok_rechazado):", "tok_aprobado")

titulo("US-17 — Ver recibo (opción 10)")
menu("10")
responder("Id de la reserva:", res)

titulo("US-17 — Calificar (opción 11)")
menu("11")
responder("Id de la reserva:", res)
responder("(1 a 5):", "5")
responder("(1 a 5):", "4")
responder("Comentario:", "Muy puntuales")
menu("0")

titulo("US-17 — Métricas de calidad (opción 18)")
login("admin@hwmoving.com", "Admin123*")
menu("18")
menu("0")
menu("0")
app.expect(pexpect.EOF)

titulo(
    f"IDS: conductor {carlos} · auxiliar {pedro} · cotización {sol} · reserva {res}"
)
