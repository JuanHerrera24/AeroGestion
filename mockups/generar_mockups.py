"""Genera los 24 mockups (HTML) de las historias de usuario de AeroGestión."""
import os, sys

SALIDA = sys.argv[1]
os.makedirs(SALIDA, exist_ok=True)

CSS = """
*{box-sizing:border-box;margin:0;padding:0}
body{font-family:'Segoe UI',Arial,sans-serif;background:#eef2f7;color:#1f2937;width:1200px;height:780px;overflow:hidden}
.top{height:58px;background:#0b3d6e;color:#fff;display:flex;align-items:center;padding:0 28px;gap:28px}
.logo{font-weight:700;font-size:21px;letter-spacing:.3px}.logo span{color:#7cc4ff}
.nav{display:flex;gap:22px;font-size:14px;opacity:.9}.nav b{border-bottom:2px solid #7cc4ff;padding-bottom:3px}
.user{margin-left:auto;font-size:13px;background:#ffffff1f;padding:6px 12px;border-radius:16px}
.main{padding:24px 32px;height:722px;position:relative}
h1{font-size:23px;margin-bottom:4px}.sub{color:#6b7280;font-size:14px;margin-bottom:18px}
.card{background:#fff;border-radius:12px;box-shadow:0 1px 4px #0b3d6e1f;padding:20px}
.grid2{display:grid;grid-template-columns:1fr 1fr;gap:16px}.grid3{display:grid;grid-template-columns:repeat(3,1fr);gap:16px}
.grid4{display:grid;grid-template-columns:repeat(4,1fr);gap:14px}.grid5{display:grid;grid-template-columns:repeat(5,1fr);gap:12px}
label{display:block;font-size:12px;font-weight:600;color:#374151;margin-bottom:5px}
.in{border:1px solid #cbd5e1;border-radius:8px;padding:10px 12px;font-size:14px;background:#fff;color:#111827;height:40px;display:flex;align-items:center}
.in.err{border-color:#dc2626;background:#fef2f2}.hint{font-size:12px;color:#dc2626;margin-top:4px}
.f{margin-bottom:12px}
.btn{display:inline-flex;align-items:center;justify-content:center;height:40px;padding:0 20px;border-radius:8px;font-weight:600;font-size:14px;background:#0b6bcb;color:#fff}
.btn.sec{background:#fff;color:#0b6bcb;border:1px solid #0b6bcb}.btn.red{background:#dc2626}.btn.amb{background:#d97706}.btn.grn{background:#16a34a}
table{width:100%;border-collapse:collapse;font-size:13px}th{text-align:left;color:#6b7280;font-weight:600;padding:9px 10px;border-bottom:1px solid #e5e7eb;font-size:12px;text-transform:uppercase;letter-spacing:.3px}
td{padding:10px;border-bottom:1px solid #f1f5f9}
.tag{display:inline-block;padding:3px 9px;border-radius:12px;font-size:11px;font-weight:700}
.ok{background:#dcfce7;color:#166534}.warn{background:#fef3c7;color:#92400e}.bad{background:#fee2e2;color:#991b1b}.info{background:#dbeafe;color:#1e40af}.gray{background:#e5e7eb;color:#374151}
.alert{border-radius:10px;padding:12px 16px;font-size:14px;margin-bottom:14px}
.a-err{background:#fef2f2;border:1px solid #fecaca;color:#991b1b}.a-ok{background:#f0fdf4;border:1px solid #bbf7d0;color:#166534}.a-warn{background:#fffbeb;border:1px solid #fde68a;color:#92400e}
.kpi .n{font-size:30px;font-weight:700;color:#0b3d6e}.kpi .l{font-size:13px;color:#6b7280}
.hu{position:absolute;left:32px;bottom:14px;font-size:12px;color:#fff;background:#0b3d6e;padding:5px 12px;border-radius:14px}
.muted{color:#6b7280}.big{font-size:20px;font-weight:700}.price{font-size:22px;font-weight:700;color:#0b6bcb}
.row{display:flex;align-items:center;gap:12px}.sp{flex:1}
.seat{width:30px;height:28px;border-radius:6px 6px 4px 4px;display:flex;align-items:center;justify-content:center;font-size:10px;font-weight:700}
.s-pri{background:#ede9fe;color:#5b21b6;border:1px solid #c4b5fd}.s-eje{background:#dbeafe;color:#1e40af;border:1px solid #93c5fd}.s-eco{background:#dcfce7;color:#166534;border:1px solid #86efac}
.s-occ{background:#d1d5db;color:#6b7280;border:1px solid #9ca3af}.s-sel{background:#0b6bcb;color:#fff;border:1px solid #0b6bcb}
.modal-bg{position:absolute;inset:0;background:#0f172a80;display:flex;align-items:center;justify-content:center}
.modal{background:#fff;border-radius:14px;padding:26px;width:440px;box-shadow:0 10px 30px #0006}
.step{display:flex;align-items:center;gap:10px}.dot{width:34px;height:34px;border-radius:50%;display:flex;align-items:center;justify-content:center;font-weight:700;font-size:13px}
.line{flex:1;height:3px;background:#cbd5e1}
"""

ROLES = {
    "visitante": (["<b>Vuelos</b>", "Aeropuertos", "Registrarse"], "Visitante · Iniciar sesión"),
    "pasajero": (["Vuelos", "<b>Mis reservas</b>", "Mi perfil"], "Carlos Ramírez · Pasajero"),
    "admin": (["<b>Panel</b>", "Flota", "Vuelos"], "Ana Torres · Administrador"),
}


def pagina(hu, titulo_hu, rol, cuerpo, nav_activo=None):
    items, usuario = ROLES[rol]
    if nav_activo:
        items = [f"<b>{i}</b>" if i == nav_activo else i for i in [x.replace("<b>", "").replace("</b>", "") for x in items]]
    html = f"""<!doctype html><html lang="es"><head><meta charset="utf-8"><style>{CSS}</style></head><body>
<div class="top"><div class="logo">✈ Aero<span>Gestión</span></div><div class="nav">{''.join(f'<div>{i}</div>' for i in items)}</div><div class="user">{usuario}</div></div>
<div class="main">{cuerpo}<div class="hu">{hu} · {titulo_hu}</div></div></body></html>"""
    open(os.path.join(SALIDA, f"{hu}.html"), "w", encoding="utf-8").write(html)


def campo(etq, valor, err=None):
    return f'<div class="f"><label>{etq}</label><div class="in{" err" if err else ""}">{valor}</div>{f"<div class=hint>{err}</div>" if err else ""}</div>'


def asiento(clase, num, est=""):
    c = {"P": "s-pri", "E": "s-eje", "C": "s-eco"}[clase]
    if est == "x":
        c = "s-occ"
    if est == "s":
        c = "s-sel"
    return f'<div class="seat {c}">{num}</div>'


VUELOS = [
    ("AG101-05", "LIM", "Lima", "CUZ", "Cusco", "24/11 06:00", "07:20", "1h 20m", 89, 144, "A320neo"),
    ("AG103-05", "LIM", "Lima", "AQP", "Arequipa", "24/11 13:00", "14:30", "1h 30m", 79, 144, "A320neo"),
    ("AG105-05", "LIM", "Lima", "BOG", "Bogotá", "24/11 08:00", "11:30", "3h 30m", 320, 152, "737-800"),
    ("AG107-05", "LIM", "Lima", "SCL", "Santiago", "24/11 07:00", "10:45", "3h 45m", 280, 172, "A321neo"),
    ("AG109-05", "LIM", "Lima", "MEX", "Ciudad de México", "24/11 01:00", "07:00", "6h 0m", 450, 220, "787-8"),
]

# ---------------------------------------------------------------- E1
pagina("HU-01", "Registro de pasajero", "visitante", f"""
<div style="width:620px;margin:0 auto"><h1>Crear tu cuenta</h1><div class="sub">Regístrate para reservar vuelos y elegir tu asiento.</div>
<div class="card"><div class="grid2">{campo('Nombre','María')}{campo('Apellido','López')}</div>
<div class="grid2">{campo('Email','maria.lopez@correo.com','El email ya está en uso')}{campo('Documento de identidad','72345678')}</div>
<div class="grid2">{campo('Teléfono','987 123 456')}{campo('Fecha de nacimiento','21/08/1999')}</div>
<div class="grid2">{campo('Nacionalidad','Peruana')}{campo('Contraseña','•••','Debe tener al menos 8 caracteres')}</div>
<div class="row" style="margin-top:6px"><div class="muted" style="font-size:13px">¿Ya tienes cuenta? <b style="color:#0b6bcb">Inicia sesión</b></div><div class="sp"></div><div class="btn">Crear cuenta</div></div></div></div>""", "Registrarse")

pagina("HU-02", "Inicio de sesión", "visitante", f"""
<div style="width:420px;margin:40px auto"><div class="card" style="padding:30px"><h1 style="text-align:center">Iniciar sesión</h1><div class="sub" style="text-align:center">Pasajeros y administradores</div>
<div class="alert a-err">Credenciales inválidas</div>{campo('Email','pasajero@aerogestion.com')}{campo('Contraseña','••••••••••••')}
<div class="btn" style="width:100%;margin-top:6px">Ingresar</div><div class="muted" style="font-size:13px;text-align:center;margin-top:14px">¿No tienes cuenta? <b style="color:#0b6bcb">Regístrate</b></div></div></div>""")

pagina("HU-15", "Consultar mi perfil", "pasajero", f"""
<h1>Mi perfil</h1><div class="sub">Datos de la cuenta con la que iniciaste sesión.</div>
<div class="card" style="width:560px"><div class="row" style="margin-bottom:18px"><div style="width:64px;height:64px;border-radius:50%;background:#0b6bcb;color:#fff;display:flex;align-items:center;justify-content:center;font-size:24px;font-weight:700">CR</div>
<div><div class="big">Carlos Ramírez</div><span class="tag info">PASAJERO</span></div></div>
<table><tr><td class="muted">Nombre</td><td><b>Carlos</b></td></tr><tr><td class="muted">Apellido</td><td><b>Ramírez</b></td></tr>
<tr><td class="muted">Email</td><td><b>pasajero@aerogestion.com</b></td></tr><tr><td class="muted">Rol</td><td><b>PASAJERO</b></td></tr>
<tr><td class="muted">Contraseña</td><td class="muted">Por seguridad no se muestra</td></tr></table></div>""", "Mi perfil")

# ---------------------------------------------------------------- E2
pagina("HU-03", "Registrar avión", "admin", f"""
<h1>Registrar avión</h1><div class="sub">El avión queda ACTIVO y disponible para programar vuelos.</div>
<div class="grid2"><div class="card">{campo('Matrícula','OB-2101','Ya existe un avión con la matrícula OB-2101')}
<div class="grid2">{campo('Fabricante','Embraer')}{campo('Modelo','E195-E2')}</div>{campo('Año de fabricación','2024')}
<div class="grid3">{campo('Filas Primera','0')}{campo('Filas Ejecutiva','3')}{campo('Filas Económica','20')}</div>
<div class="row"><div class="sp"></div><div class="btn sec">Cancelar</div><div class="btn">Guardar avión</div></div></div>
<div class="card"><b>Flota actual</b><table style="margin-top:8px"><tr><th>Matrícula</th><th>Modelo</th><th>Capacidad</th><th>Estado</th></tr>
<tr><td>OB-2101</td><td>Airbus A320neo</td><td>144</td><td><span class="tag ok">ACTIVO</span></td></tr>
<tr><td>OB-1987</td><td>Boeing 737-800</td><td>152</td><td><span class="tag ok">ACTIVO</span></td></tr>
<tr><td>OB-2245</td><td>Airbus A321neo</td><td>172</td><td><span class="tag ok">ACTIVO</span></td></tr>
<tr><td>OB-1790</td><td>Boeing 787-8</td><td>220</td><td><span class="tag ok">ACTIVO</span></td></tr>
<tr><td>OB-1655</td><td>Airbus A319</td><td>120</td><td><span class="tag warn">EN MANTENIMIENTO</span></td></tr></table></div></div>""", "Flota")

pagina("HU-04", "Actualizar estado de un avión", "admin", f"""
<h1>OB-2101 · Airbus A320neo</h1><div class="sub">Cambia el estado del avión. Solo los aviones ACTIVOS se asignan a vuelos.</div>
<div class="grid2"><div class="card"><label>Estado actual</label><div style="margin-bottom:14px"><span class="tag ok">ACTIVO</span></div>
<label>Nuevo estado</label><div class="row" style="margin-bottom:16px"><div class="btn sec">ACTIVO</div><div class="btn amb">EN MANTENIMIENTO</div><div class="btn sec">FUERA DE SERVICIO</div></div>
<div class="btn" style="width:100%">Guardar estado</div></div>
<div class="card"><div class="alert a-warn"><b>Atención:</b> el avión tiene vuelos futuros programados.</div><b>Vuelos afectados</b>
<table style="margin-top:8px"><tr><th>Vuelo</th><th>Ruta</th><th>Salida</th></tr><tr><td>AG101-05</td><td>LIM → CUZ</td><td>24/11 06:00</td></tr>
<tr><td>AG102-05</td><td>CUZ → LIM</td><td>24/11 09:00</td></tr><tr><td>AG103-05</td><td>LIM → AQP</td><td>24/11 13:00</td></tr><tr><td>AG104-05</td><td>AQP → LIM</td><td>24/11 17:00</td></tr></table></div></div>""", "Flota")

filas_mapa = ""
for f, cl, n in [(1, "P", 4), (2, "P", 4), (3, "E", 4), (4, "E", 4), (7, "C", 6), (8, "C", 6)]:
    letras = "ACDF" if n == 4 else "ABCDEF"
    filas_mapa += f'<div class="row" style="gap:6px;margin-bottom:6px"><div style="width:20px;font-size:11px" class="muted">{f}</div>' + "".join(asiento(cl, f"{f}{l}") for l in letras) + "</div>"
pagina("HU-05", "Configurar mapa de asientos", "admin", f"""
<h1>Configurar asientos · OB-2399</h1><div class="sub">Las filas se numeran en orden: Primera, Ejecutiva y Económica.</div>
<div class="grid2"><div class="card"><div class="grid3">{campo('Filas Primera','2')}{campo('Filas Ejecutiva','2')}{campo('Filas Económica','18')}</div>
<table style="margin:6px 0 14px"><tr><th>Clase</th><th>Asientos por fila</th><th>Factor de precio</th><th>Total</th></tr>
<tr><td>Primera</td><td>4 (A C D F)</td><td>× 3.0</td><td>8</td></tr><tr><td>Ejecutiva</td><td>4 (A C D F)</td><td>× 1.8</td><td>8</td></tr>
<tr><td>Económica</td><td>6 (A–F)</td><td>× 1.0</td><td>108</td></tr><tr><td><b>Capacidad</b></td><td></td><td></td><td><b>124</b></td></tr></table>
<div class="btn" style="width:100%">Generar asientos</div></div>
<div class="card"><b>Vista previa</b><div style="margin-top:12px">{filas_mapa}</div><div class="row" style="font-size:12px;margin-top:10px">{asiento('P','')} Primera {asiento('E','')} Ejecutiva {asiento('C','')} Económica</div></div></div>""", "Flota")

pagina("HU-16", "Listar la flota", "admin", f"""
<h1>Flota</h1><div class="sub">5 aviones registrados · 4 activos</div>
<div class="card"><table><tr><th>Matrícula</th><th>Fabricante</th><th>Modelo</th><th>Año</th><th>Primera</th><th>Ejecutiva</th><th>Económica</th><th>Capacidad</th><th>Estado</th></tr>
<tr><td><b>OB-2101</b></td><td>Airbus</td><td>A320neo</td><td>2021</td><td>8</td><td>16</td><td>120</td><td><b>144</b></td><td><span class="tag ok">ACTIVO</span></td></tr>
<tr><td><b>OB-1987</b></td><td>Boeing</td><td>737-800</td><td>2018</td><td>8</td><td>12</td><td>132</td><td><b>152</b></td><td><span class="tag ok">ACTIVO</span></td></tr>
<tr><td><b>OB-2245</b></td><td>Airbus</td><td>A321neo</td><td>2023</td><td>12</td><td>16</td><td>144</td><td><b>172</b></td><td><span class="tag ok">ACTIVO</span></td></tr>
<tr><td><b>OB-1790</b></td><td>Boeing</td><td>787-8 Dreamliner</td><td>2019</td><td>16</td><td>24</td><td>180</td><td><b>220</b></td><td><span class="tag ok">ACTIVO</span></td></tr>
<tr><td><b>OB-1655</b></td><td>Airbus</td><td>A319</td><td>2015</td><td>0</td><td>12</td><td>108</td><td><b>120</b></td><td><span class="tag warn">EN MANTENIMIENTO</span></td></tr></table>
<div class="row" style="margin-top:16px"><div class="sp"></div><div class="btn">+ Registrar avión</div></div></div>""", "Flota")

pagina("HU-17", "Ver el detalle de un avión", "admin", f"""
<div class="row" style="margin-bottom:16px"><div class="in" style="width:320px">🔍  OB-1790</div><div class="btn">Buscar</div></div>
<div class="grid2"><div class="card"><div class="row"><div><div class="big">OB-1790</div><div class="muted">Boeing 787-8 Dreamliner · 2019</div></div><div class="sp"></div><span class="tag ok">ACTIVO</span></div>
<table style="margin-top:14px"><tr><td class="muted">Fabricante</td><td><b>Boeing</b></td></tr><tr><td class="muted">Modelo</td><td><b>787-8 Dreamliner</b></td></tr>
<tr><td class="muted">Año</td><td><b>2019</b></td></tr><tr><td class="muted">Capacidad total</td><td><b>220 asientos</b></td></tr></table></div>
<div class="card"><b>Asientos por clase</b><div class="grid3" style="margin-top:14px">
<div class="card kpi" style="box-shadow:none;background:#f5f3ff"><div class="n" style="color:#5b21b6">16</div><div class="l">Primera</div></div>
<div class="card kpi" style="box-shadow:none;background:#eff6ff"><div class="n" style="color:#1e40af">24</div><div class="l">Ejecutiva</div></div>
<div class="card kpi" style="box-shadow:none;background:#f0fdf4"><div class="n" style="color:#166534">180</div><div class="l">Económica</div></div></div>
<div class="alert a-err" style="margin-top:16px">Si la matrícula no existe: <b>«No existe el avión OB-0000»</b></div></div></div>""", "Flota")

# ---------------------------------------------------------------- E3
pagina("HU-06", "Programar vuelo", "admin", f"""
<h1>Programar vuelo</h1><div class="sub">El vuelo queda PROGRAMADO y visible para los pasajeros.</div>
<div class="grid2"><div class="card"><div class="grid2">{campo('Código de vuelo','AG900')}{campo('Tarifa base (S/)','250.00')}</div>
<div class="grid2">{campo('Origen','LIM · Lima')}{campo('Destino','SCL · Santiago')}</div>
<div class="grid2">{campo('Salida','15/12/2026 09:00')}{campo('Llegada','15/12/2026 12:45')}</div>
{campo('Avión (solo activos)','OB-2399 · Embraer E195-E2')}<div class="row"><div class="sp"></div><div class="btn sec">Cancelar</div><div class="btn">Programar vuelo</div></div></div>
<div class="card"><b>Validaciones</b><div style="margin-top:12px;font-size:14px;line-height:2">✅ Origen y destino distintos<br>✅ La llegada es posterior a la salida<br>✅ La salida es una fecha futura<br>✅ El avión está ACTIVO y tiene asientos<br>✅ El avión no tiene otro vuelo en ese horario</div>
<div class="alert a-err" style="margin-top:12px">Ejemplo de error: «El avión OB-1655 no está activo»</div></div></div>""", "Vuelos")

pagina("HU-07", "Modificar o cancelar vuelo", "admin", f"""
<h1>Vuelo AG900 · Lima → Santiago</h1><div class="sub">Edita el horario, retrasa o cancela el vuelo.</div>
<div class="card" style="width:640px"><div class="grid2">{campo('Salida','15/12/2026 10:00')}{campo('Llegada','15/12/2026 13:45')}</div>
<div class="grid2">{campo('Avión','OB-2399 · Embraer E195-E2')}{campo('Tarifa base (S/)','260.00')}</div>
<div class="row"><span class="tag info">PROGRAMADO</span><div class="sp"></div><div class="btn amb">Retrasar</div><div class="btn red">Cancelar vuelo</div><div class="btn">Guardar cambios</div></div></div>
<div class="modal-bg"><div class="modal"><div class="big" style="margin-bottom:8px">¿Cancelar el vuelo AG900?</div>
<div class="muted" style="font-size:14px;margin-bottom:16px">Se cancelarán sus <b>3 reservas confirmadas</b> y se enviará un correo a cada pasajero.</div>
<div class="row"><div class="sp"></div><div class="btn sec">Volver</div><div class="btn red">Sí, cancelar vuelo</div></div></div></div>""", "Vuelos")

pagina("HU-08", "Panel de vuelos y ocupación", "admin", f"""
<h1>Panel de operaciones</h1><div class="sub">Resumen al 23/11/2026</div>
<div class="grid5" style="margin-bottom:16px"><div class="card kpi"><div class="n">5</div><div class="l">Aviones</div></div><div class="card kpi"><div class="n">4</div><div class="l">Aviones activos</div></div>
<div class="card kpi"><div class="n">70</div><div class="l">Vuelos próximos 7 días</div></div><div class="card kpi"><div class="n">12</div><div class="l">Reservas confirmadas</div></div><div class="card kpi"><div class="n">3.4%</div><div class="l">Ocupación media</div></div></div>
<div class="card"><b>Próximas salidas</b><table style="margin-top:8px"><tr><th>Vuelo</th><th>Ruta</th><th>Salida</th><th>Avión</th><th>Ocupación</th><th>Estado</th></tr>
{''.join(f'<tr><td><b>{v[0]}</b></td><td>{v[1]} → {v[3]}</td><td>{v[5]}</td><td>{v[10]}</td><td><div style="background:#e5e7eb;border-radius:6px;height:8px;width:120px"><div style="background:#0b6bcb;height:8px;border-radius:6px;width:{w}px"></div></div></td><td><span class="tag info">PROGRAMADO</span></td></tr>' for v, w in zip(VUELOS, [36, 12, 8, 20, 4]))}</table></div>""", "Panel")

pagina("HU-18", "Actualizar el estado operativo de un vuelo", "admin", f"""
<h1>Vuelo AG101-05 · Lima → Cusco</h1><div class="sub">Salida 24/11/2026 06:00 · Avión OB-2101</div>
<div class="card"><div class="step"><div class="dot" style="background:#16a34a;color:#fff">✓</div><b>PROGRAMADO</b><div class="line" style="background:#16a34a"></div>
<div class="dot" style="background:#0b6bcb;color:#fff">2</div><b style="color:#0b6bcb">EMBARCANDO</b><div class="line"></div><div class="dot" style="background:#e5e7eb">3</div><span class="muted">EN VUELO</span><div class="line"></div>
<div class="dot" style="background:#e5e7eb">4</div><span class="muted">ATERRIZADO</span></div>
<div class="row" style="margin-top:26px"><div style="width:280px">{campo('Nuevo estado','EMBARCANDO')}</div><div class="btn" style="margin-top:8px">Actualizar estado</div></div>
<div class="alert a-ok">Estado actualizado: el vuelo ya no admite reservas nuevas.</div><div class="alert a-err" style="margin-bottom:0">Si el vuelo está CANCELADO: «El vuelo está cancelado» (409)</div></div>""", "Vuelos")

pagina("HU-19", "Filtrar vuelos por estado y fechas", "admin", f"""
<h1>Vuelos</h1><div class="sub">Filtra por estado y rango de fechas.</div>
<div class="card" style="margin-bottom:14px"><div class="row" style="align-items:flex-end"><div style="width:220px">{campo('Estado','RETRASADO')}</div><div style="width:200px">{campo('Desde','24/11/2026')}</div>
<div style="width:200px">{campo('Hasta','26/11/2026')}</div><div class="btn" style="margin-bottom:12px">Filtrar</div><div class="btn sec" style="margin-bottom:12px">Limpiar</div></div></div>
<div class="card"><table><tr><th>Vuelo</th><th>Ruta</th><th>Salida</th><th>Avión</th><th>Estado</th></tr>
<tr><td><b>AG105-05</b></td><td>LIM → BOG</td><td>24/11 08:00</td><td>OB-1987</td><td><span class="tag warn">RETRASADO</span></td></tr>
<tr><td><b>AG108-06</b></td><td>SCL → LIM</td><td>25/11 13:00</td><td>OB-2245</td><td><span class="tag warn">RETRASADO</span></td></tr>
<tr><td><b>AG102-07</b></td><td>CUZ → LIM</td><td>26/11 09:00</td><td>OB-2101</td><td><span class="tag warn">RETRASADO</span></td></tr></table><div class="muted" style="font-size:13px;margin-top:10px">3 vuelos encontrados</div></div>""", "Vuelos")

# ---------------------------------------------------------------- E4
def resultado(v, sel=False):
    return f"""<div class="card" style="margin-bottom:10px;{'border:2px solid #0b6bcb' if sel else ''}"><div class="row"><div style="width:110px"><b>{v[0]}</b><div class="muted" style="font-size:12px">{v[10]}</div></div>
<div style="width:90px"><div class="big">{v[5][-5:]}</div><div class="muted">{v[1]}</div></div><div style="width:110px;text-align:center" class="muted">{v[7]}<div style="border-top:2px dashed #cbd5e1;margin:4px 0"></div>Directo</div>
<div style="width:90px"><div class="big">{v[6]}</div><div class="muted">{v[3]}</div></div><div class="sp"></div><div style="text-align:right"><div class="muted" style="font-size:12px">desde</div><div class="price">S/ {v[8]:.2f}</div></div><div class="btn" style="margin-left:14px">Ver vuelo</div></div></div>"""

buscador = lambda o, d, f: f"""<div class="card" style="margin-bottom:14px"><div class="row" style="align-items:flex-end"><div style="width:250px">{campo('Origen',o)}</div><div style="width:250px">{campo('Destino',d)}</div><div style="width:190px">{campo('Fecha',f)}</div><div class="btn" style="margin-bottom:12px">Buscar vuelos</div></div></div>"""

pagina("HU-09", "Buscar vuelos", "visitante", f"""<h1>Buscar vuelos</h1><div class="sub">Encuentra tu vuelo por origen, destino y fecha.</div>{buscador('LIM · Lima','CUZ · Cusco','24/11/2026')}
<div class="muted" style="margin-bottom:8px;font-size:14px">1 vuelo encontrado · Lima → Cusco · martes 24 de noviembre</div>{resultado(VUELOS[0])}
<div class="alert a-warn" style="margin-top:14px">Si no hay vuelos: «No hay vuelos disponibles para esta búsqueda»</div>""")

pagina("HU-10", "Ver detalle de un vuelo", "visitante", f"""
<h1>AG101-05 · Lima → Cusco</h1><div class="sub">Martes 24/11/2026 · Airbus A320neo (OB-2101) · Directo</div>
<div class="card" style="margin-bottom:16px"><div class="row"><div><div class="big">06:00</div><div class="muted">LIM · Aeropuerto Internacional Jorge Chávez</div></div><div class="sp" style="text-align:center" class="muted">1h 20m<div style="border-top:2px dashed #cbd5e1;margin:6px 40px"></div></div>
<div style="text-align:right"><div class="big">07:20</div><div class="muted">CUZ · Aeropuerto Alejandro Velasco Astete</div></div></div></div>
<div class="grid3"><div class="card" style="border-top:4px solid #7c3aed"><b>Primera</b><div class="price">S/ 267.00</div><div class="muted">8 de 8 disponibles</div></div>
<div class="card" style="border-top:4px solid #2563eb"><b>Ejecutiva</b><div class="price">S/ 160.20</div><div class="muted">16 de 16 disponibles</div></div>
<div class="card" style="border-top:4px solid #16a34a"><b>Económica</b><div class="price">S/ 89.00</div><div class="muted">120 de 120 disponibles</div></div></div>
<div class="row" style="margin-top:18px"><div class="sp"></div><div class="btn">Elegir asientos</div></div>""")

pagina("HU-20", "Ver próximas salidas", "visitante", f"""
<h1>Próximas salidas</h1><div class="sub">Los próximos vuelos abiertos para reservar.</div>
<div class="grid3">{''.join(f'<div class="card"><div class="row"><b>{v[0]}</b><div class="sp"></div><span class="tag info">PROGRAMADO</span></div><div class="big" style="margin:10px 0 2px">{v[2]} → {v[4]}</div><div class="muted">{v[5]} · {v[7]}</div><div class="row" style="margin-top:12px"><div><div class="muted" style="font-size:12px">desde</div><div class="price">S/ {v[8]:.2f}</div></div><div class="sp"></div><div class="muted" style="font-size:13px">{v[9]} asientos libres</div></div></div>' for v in VUELOS[:5])}
<div class="card" style="display:flex;align-items:center;justify-content:center;color:#0b6bcb;font-weight:600">Ver todos los vuelos →</div></div>""")

pagina("HU-21", "Consultar los aeropuertos disponibles", "visitante", f"""
<h1>Buscar vuelos</h1><div class="sub">Elige tu origen entre los aeropuertos donde operamos.</div>
<div class="card" style="width:560px"><label>Origen</label><div class="in" style="border-color:#0b6bcb">Selecciona un aeropuerto ▾</div>
<div class="card" style="margin-top:6px;padding:6px 0"><table>{''.join(f'<tr><td style="width:60px"><b>{c}</b></td><td>{ci}</td><td class="muted">{pa}</td></tr>' for c, ci, pa in [('LIM','Lima','Perú'),('CUZ','Cusco','Perú'),('AQP','Arequipa','Perú'),('BOG','Bogotá','Colombia'),('SCL','Santiago','Chile'),('MEX','Ciudad de México','México')])}</table></div></div>""", "Aeropuertos")

pagina("HU-22", "Ordenar los resultados de búsqueda", "visitante", f"""<h1>Buscar vuelos</h1><div class="sub">Lima → todos los destinos · 24/11/2026</div>
<div class="row" style="margin-bottom:12px"><span class="muted" style="font-size:14px">Ordenar por:</span><div class="btn">Precio ↑</div><div class="btn sec">Hora de salida</div></div>
{''.join(resultado(v) for v in sorted(VUELOS[:4], key=lambda x: x[8]))}""")

# ---------------------------------------------------------------- E5
mapa = ""
ocup = {"1C", "2A", "3D", "7B", "7C", "8E", "9A", "9F"}
for f, cl in [(1, "P"), (2, "P"), (3, "E"), (4, "E"), (5, "E"), (6, "E"), (7, "C"), (8, "C"), (9, "C"), (10, "C")]:
    letras = "ACDF" if cl in "PE" else "ABCDEF"
    asientos = []
    for i, l in enumerate(letras):
        num = f"{f}{l}"
        est = "x" if num in ocup else ("s" if num in ("1A", "10C") else "")
        asientos.append(asiento(cl, num, est))
        if (cl in "PE" and i == 1) or (cl == "C" and i == 2):
            asientos.append('<div style="width:26px"></div>')
    mapa += f'<div class="row" style="gap:6px;margin-bottom:5px;justify-content:center"><div style="width:18px;font-size:11px" class="muted">{f}</div>{"".join(asientos)}</div>'
pagina("HU-11", "Seleccionar asiento", "pasajero", f"""
<div class="row" style="align-items:flex-start;gap:20px"><div class="card" style="width:560px"><div class="row"><b>AG101-05 · Lima → Cusco · Airbus A320neo</b></div>
<div style="margin-top:14px">{mapa}</div><div class="row" style="font-size:12px;margin-top:10px;justify-content:center;gap:8px">{asiento('P','')}Primera S/ 267 {asiento('E','')}Ejecutiva S/ 160.20 {asiento('C','')}Económica S/ 89 {asiento('C','','x')}Ocupado {asiento('C','','s')}Tu selección</div></div>
<div class="card sp"><div class="big">Tu selección</div><table style="margin:12px 0"><tr><th>Asiento</th><th>Clase</th><th>Precio</th></tr><tr><td><b>1A</b></td><td>Primera</td><td>S/ 267.00</td></tr><tr><td><b>10C</b></td><td>Económica</td><td>S/ 89.00</td></tr><tr><td colspan="2"><b>Total</b></td><td><b>S/ 356.00</b></td></tr></table>
<div class="alert a-err">El asiento 1C ya está ocupado</div><div class="btn" style="width:100%">Continuar a la reserva</div></div></div>""", "Vuelos")

pagina("HU-12", "Confirmar reserva", "pasajero", f"""
<div style="width:640px;margin:0 auto"><div class="alert a-ok" style="font-size:16px"><b>¡Reserva confirmada!</b> Te enviamos un correo con tu código.</div>
<div class="card"><div class="row"><div><div class="muted" style="font-size:12px">CÓDIGO DE RESERVA</div><div style="font-size:30px;font-weight:800;letter-spacing:2px;color:#0b3d6e">AG-7K2P9X</div></div><div class="sp"></div><span class="tag ok">CONFIRMADA</span></div>
<table style="margin-top:14px"><tr><td class="muted">Vuelo</td><td><b>AG101-05</b> · Lima → Cusco</td></tr><tr><td class="muted">Salida</td><td><b>Martes 24/11/2026 · 06:00</b></td></tr><tr><td class="muted">Pasajero</td><td><b>Carlos Ramírez</b></td></tr>
<tr><td class="muted">Asientos</td><td><b>1A</b> Primera S/ 267.00 · <b>10C</b> Económica S/ 89.00</td></tr><tr><td class="muted">Total</td><td class="price">S/ 356.00</td></tr></table>
<div class="row" style="margin-top:14px"><div class="sp"></div><div class="btn sec">Ver mis reservas</div><div class="btn">Ir al inicio</div></div></div></div>""", "Mis reservas")

pagina("HU-13", "Consultar mis reservas", "pasajero", f"""
<h1>Mis reservas</h1><div class="sub">Solo ves tus propias reservas, las más recientes primero.</div>
<div class="card"><table><tr><th>Código</th><th>Vuelo</th><th>Ruta</th><th>Salida</th><th>Asientos</th><th>Total</th><th>Estado</th><th></th></tr>
<tr><td><b>AG-7K2P9X</b></td><td>AG101-05</td><td>LIM → CUZ</td><td>24/11 06:00</td><td>1A, 10C</td><td>S/ 356.00</td><td><span class="tag ok">CONFIRMADA</span></td><td style="color:#0b6bcb;font-weight:600">Ver</td></tr>
<tr><td><b>AG-92R3B5</b></td><td>AG103-03</td><td>LIM → AQP</td><td>22/11 13:00</td><td>8A, 8B</td><td>S/ 158.00</td><td><span class="tag ok">CONFIRMADA</span></td><td style="color:#0b6bcb;font-weight:600">Ver</td></tr>
<tr><td><b>AG-H4M8QW</b></td><td>AG107-02</td><td>LIM → SCL</td><td>21/11 07:00</td><td>12B</td><td>S/ 280.00</td><td><span class="tag bad">CANCELADA</span></td><td style="color:#0b6bcb;font-weight:600">Ver</td></tr></table></div>""", "Mis reservas")

pagina("HU-23", "Ver el detalle de una reserva", "pasajero", f"""
<h1>Reserva AG-7K2P9X</h1><div class="sub">Detalle de tu reserva</div>
<div class="grid2"><div class="card"><table><tr><td class="muted">Vuelo</td><td><b>AG101-05</b></td></tr><tr><td class="muted">Ruta</td><td><b>LIM → CUZ</b></td></tr><tr><td class="muted">Salida</td><td><b>24/11/2026 06:00</b></td></tr>
<tr><td class="muted">Reservada el</td><td><b>19/11/2026 18:42</b></td></tr><tr><td class="muted">Estado</td><td><span class="tag ok">CONFIRMADA</span></td></tr><tr><td class="muted">Cancelable</td><td><span class="tag ok">SÍ · hasta el 23/11 06:00</span></td></tr></table></div>
<div class="card"><b>Asientos</b><table style="margin:8px 0 14px"><tr><th>Asiento</th><th>Clase</th><th>Precio</th></tr><tr><td>1A</td><td>Primera</td><td>S/ 267.00</td></tr><tr><td>10C</td><td>Económica</td><td>S/ 89.00</td></tr></table>
<div class="row"><b>Total</b><div class="sp"></div><div class="price">S/ 356.00</div></div><div class="btn red" style="width:100%;margin-top:14px">Cancelar reserva</div></div></div>""", "Mis reservas")

pagina("HU-14", "Cancelar reserva", "pasajero", f"""
<h1>Reserva AG-7K2P9X</h1><div class="sub">AG101-05 · Lima → Cusco · 24/11/2026 06:00</div>
<div class="card" style="width:640px"><div class="row"><b>Asientos 1A, 10C</b><div class="sp"></div><div class="price">S/ 356.00</div></div></div>
<div class="modal-bg"><div class="modal"><div class="big" style="margin-bottom:8px">¿Cancelar la reserva AG-7K2P9X?</div>
<div class="muted" style="font-size:14px;margin-bottom:12px">Los asientos 1A y 10C quedarán libres y recibirás un correo de confirmación.</div>
<div class="alert a-warn" style="font-size:13px">Solo puedes cancelar hasta 24 horas antes de la salida del vuelo.</div>
<div class="row"><div class="sp"></div><div class="btn sec">Volver</div><div class="btn red">Sí, cancelar</div></div></div></div>""", "Mis reservas")

pagina("HU-24", "Recibir aviso si la aerolínea cancela mi vuelo", "pasajero", f"""
<h1>Bandeja de entrada</h1><div class="sub">Correo que recibe el pasajero</div>
<div class="card" style="width:760px"><div class="row" style="border-bottom:1px solid #e5e7eb;padding-bottom:12px"><div style="width:44px;height:44px;border-radius:50%;background:#0b3d6e;color:#fff;display:flex;align-items:center;justify-content:center;font-weight:700">AG</div>
<div><b>AeroGestión</b> <span class="muted">&lt;no-reply@aerogestion.com&gt;</span><div class="muted" style="font-size:13px">Para: pasajero@aerogestion.com</div></div><div class="sp"></div><span class="muted" style="font-size:13px">23/11/2026 20:15</span></div>
<div class="big" style="margin:16px 0 10px">Tu reserva AG-7K2P9X fue cancelada</div>
<div style="font-size:15px;line-height:1.7">Hola Carlos,<br><br>Lamentamos informarte que el vuelo <b>AG101-05 Lima → Cusco</b> del <b>24/11/2026 a las 06:00</b> fue <b>cancelado por la aerolínea</b>.<br>
Tu reserva <b>AG-7K2P9X</b> (asientos 1A y 10C) quedó en estado <span class="tag bad">CANCELADA</span>.<br><br>Puedes buscar un nuevo vuelo en AeroGestión.<br><br>Equipo AeroGestión</div></div>""", "Mis reservas")

print(len([f for f in os.listdir(SALIDA) if f.endswith('.html')]), "mockups HTML")
