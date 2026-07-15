# Sistema de Gestión de Inventario — Telecomunicaciones

Proyecto Java web (Spring Boot + MySQL) con arquitectura MVC, para gestionar el
inventario de una empresa de telecomunicaciones: productos, proveedores, tipos
de almacén, historial de movimientos, y ahora también **login con roles**
(Admin / Encargado de almacén).

---

## 1. Requisitos previos

| Herramienta | Uso | ¿Cómo verificar si ya lo tienes? |
|---|---|---|
| **JDK 17 o superior** | Compilar y correr el backend | En una terminal: `java -version` |
| **XAMPP (MySQL)** | Base de datos | Abre el panel de XAMPP y arranca el módulo **MySQL** |
| **VSCode** | Editor | — |
| Extensión **Extension Pack for Java** (Microsoft) | Compilar/ejecutar Java desde VSCode (ya incluye Maven, no necesitas instalarlo aparte) | Buscar en la pestaña de Extensiones de VSCode |
| Extensión **Spring Boot Extension Pack** (opcional, recomendable) | Autocompletado y ejecución más cómoda de Spring Boot | Buscar en la pestaña de Extensiones |

Si `java -version` no funciona, instala el JDK 17 (Temurin/Adoptium es una
buena opción gratuita) y reinicia VSCode.

---

## 2. Configurar el correo del Admin (para el código de verificación)

Cuando alguien se registra como **Encargado**, el sistema le pide un código de
6 dígitos que se envía **por correo real (Gmail)** a la cuenta del Admin.
Para que esto funcione:

1. Entra a la cuenta de Gmail del Admin (`jeffersonra040805@gmail.com`).
2. Activa la **verificación en 2 pasos** (si no la tienes activada):
   `myaccount.google.com` → **Seguridad** → **Verificación en 2 pasos**.
3. Ya con eso activo, ve a **Contraseñas de aplicaciones**
   (`myaccount.google.com/apppasswords`), crea una nueva y copia el código de
   16 caracteres que te da Google (esa NO es tu contraseña normal de Gmail).
4. Abre `src/main/resources/application.properties` y reemplaza:

   ```properties
   spring.mail.password=PEGA_AQUI_TU_CONTRASENA_DE_APLICACION
   ```

   por ese código (sin espacios).

> El correo que pusiste en la instrucción original estaba incompleto
> (`jeffersonra040805@gmail`), así que asumí `jeffersonra040805@gmail.com`.
> Si no es correcto, corrígelo en `spring.mail.username` y `app.admin.correo`
> dentro de ese mismo archivo.

Mientras no configures esto, el sistema seguirá funcionando normalmente
(login, inventario, ajustes, historial), pero el envío del código de
verificación fallará con un mensaje de error hasta que pongas la contraseña
de aplicación correcta.

---

## 3. Crear la base de datos

1. Abre **XAMPP** → inicia **MySQL** (y Apache si quieres usar phpMyAdmin).
2. Entra a `http://localhost/phpmyadmin`.
3. Crea una base de datos llamada exactamente:

   ```
   inventario_telecom
   ```

4. **No necesitas crear tablas manualmente**: al levantar el proyecto,
   Spring Boot (Hibernate) crea automáticamente las tablas `producto`,
   `proveedor`, `tipo_almacen`, `movimiento`, `usuario` y
   `codigo_verificacion`.

Si tu MySQL de XAMPP usa usuario/contraseña distintos a `root` sin contraseña,
edita `src/main/resources/application.properties` y ajusta
`spring.datasource.username` / `spring.datasource.password`.

---

## 4. Abrir y ejecutar el proyecto en VSCode

1. Abre la carpeta `inventario-telecom` completa en VSCode (`Archivo > Abrir carpeta`).
2. Espera a que la extensión de Java termine de indexar el proyecto (barra
   inferior). La primera vez descargará las dependencias nuevas (correo,
   BCrypt), necesitas internet en ese momento.
3. Abre `src/main/java/com/telecom/inventario/InventarioApplication.java`.
4. Haz clic en **Run** (▷) que aparece arriba de `public class InventarioApplication`.
5. Cuando la consola muestre `Tomcat started on port 8080` y
   `Started InventarioApplication`, el sistema ya está corriendo. Además,
   la primera vez vas a ver un log como:

   ```
   Cuenta Admin creada automaticamente (usuario: JeffersonRA)
   ```

6. Abre en tu navegador: `http://localhost:8080` → te mostrará la pantalla de
   **Inicio de sesión**.

---

## 5. Cuentas del sistema

### Admin (ya viene creada, no se registra por el formulario público)

```
Usuario:    JeffersonRA
Contraseña: jefferson2005
Correo:     jeffersonra040805@gmail.com
DNI:        75269609
Celular:    902820772
```

Esta cuenta se crea sola la primera vez que arrancas el proyecto (mira
`config/DataInitializer.java` si quieres cambiar estos datos antes del primer
arranque).

### Encargado de almacén (se registra desde "Regístrate aquí" en el login)

1. Llena sus datos (nombre, correo, DNI, celular, usuario, contraseña).
2. Da clic en **"Enviar código al Admin"** → esto manda un correo real a
   `jeffersonra040805@gmail.com` con un código de 6 dígitos válido por 15
   minutos.
3. El Admin revisa su correo y, si autoriza el registro, le pasa el código a
   la persona.
4. La persona escribe el código y da clic en **Crear cuenta**. Sin el código
   correcto, la cuenta no se crea.

---

## 6. Roles y permisos

| Función | Encargado | Admin |
|---|---|---|
| Ver / agregar / editar / eliminar productos | Sí | Sí |
| Ajustes (proveedores y tipos de almacén) | Sí | Sí |
| Ver historial de movimientos | Sí | Sí |
| **Administrar empleados** (ver, editar, eliminar cuentas) | No | **Sí (exclusivo)** |

El enlace **"Administrar empleados"** solo aparece en el menú hamburguesa
cuando inicia sesión el Admin.

> Nota de seguridad: para mantener el proyecto simple, la sesión se guarda en
> el navegador (`sessionStorage`) y el rol se envía al backend en cabeceras
> HTTP en cada petición. Esto es suficiente para un sistema de uso interno,
> pero si más adelante el sistema va a estar expuesto fuera de tu red local,
> te recomiendo pasar a un login con Spring Security + JWT para una seguridad
> más robusta. Lo podemos hacer cuando lo necesites.

---

## 7. Estructura del proyecto (MVC)

```
inventario-telecom/
├── pom.xml
├── src/main/java/com/telecom/inventario/
│   ├── InventarioApplication.java      -> arranque de la app
│   ├── config/                         -> SeguridadConfig (BCrypt), DataInitializer (crea al Admin)
│   ├── model/                          -> Producto, Proveedor, TipoAlmacen, Movimiento, Usuario, CodigoVerificacion
│   ├── dto/                            -> datos de entrada para login/registro/edición de cuentas
│   ├── repository/                     -> acceso a datos (Spring Data JPA)
│   ├── service/                        -> reglas de negocio, historial, autenticación, envío de correo
│   └── controller/                     -> endpoints REST (API)
└── src/main/resources/
    ├── application.properties          -> conexión a MySQL + configuración de correo + datos del Admin
    └── static/                         -> Vista (frontend HTML/CSS/JS)
        ├── login.html                  -> Inicio de sesión
        ├── registro.html               -> Registro de Encargado (con código de verificación)
        ├── index.html                  -> Inventario (tabla, buscar, agregar/editar/eliminar)
        ├── ajustes.html                -> Proveedores y tipos de almacén
        ├── historial.html              -> Historial de movimientos
        ├── empleados.html              -> Administrar empleados (solo Admin)
        ├── css/ (style.css, auth.css)
        └── js/ (main.js, login.js, registro.js, inventario.js, ajustes.js, historial.js, empleados.js)
```

**Flujo MVC:** el navegador (Vista) llama a la API (`/api/...`) → el
**Controller** recibe la petición → delega en el **Service** (reglas de
negocio, valida y registra el movimiento en el historial) → el **Repository**
guarda/lee en MySQL usando el **Model** (entidad JPA).

---

## 8. Endpoints de la API

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/auth/login` | Iniciar sesión |
| POST | `/api/auth/codigo` | Solicitar código de verificación (se envía al Admin) |
| POST | `/api/auth/registro` | Crear cuenta de Encargado (requiere código válido) |
| GET | `/api/empleados` | Listar cuentas (solo Admin) |
| PUT | `/api/empleados/{id}` | Editar una cuenta (solo Admin) |
| DELETE | `/api/empleados/{id}` | Eliminar una cuenta (solo Admin) |
| GET | `/api/productos?nombre=&proveedorId=&tipoAlmacenId=` | Listar / buscar productos |
| GET | `/api/productos/{id}` | Obtener un producto |
| POST | `/api/productos` | Crear producto |
| PUT | `/api/productos/{id}` | Editar producto |
| DELETE | `/api/productos/{id}` | Eliminar producto |
| GET / POST / DELETE | `/api/proveedores` | Listar / crear / eliminar proveedores |
| GET / POST / DELETE | `/api/tipos-almacen` | Listar / crear / eliminar tipos de almacén |
| GET | `/api/movimientos` | Historial de movimientos |

---

## 9. Siguientes pasos sugeridos

- Pasar la autenticación a Spring Security + JWT si el sistema saldrá de tu red local.
- Agregar exportar a Excel/PDF desde Inventario e Historial.
- Agregar alertas de stock mínimo por producto.

Cualquiera de estos puntos lo podemos construir sobre esta misma base cuando lo necesites.
