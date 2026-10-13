# API REST Demo (DAM235) + App Android

Proyecto basado en la guía **"Creación de WS API Rest"**. Consta de dos partes:

1. **Backend** (`apirestdemo`): API REST en **Spring Boot + Spring Security + JWT + MariaDB**.
2. **App Android** (`ApirestDemoAndroid`): cliente móvil que consume la API usando **Retrofit + Gson**.

La app permite iniciar sesión, registrar usuarios, cerrar sesión y realizar el CRUD de productos.

---

## Tecnologías

| Parte | Tecnologías |
|-------|-------------|
| Backend | Java 17, Spring Boot 4.1.1, Spring Security, JWT (jjwt 0.9.1), JPA/Hibernate, MariaDB, Lombok, Maven |
| Android | Java 17, Retrofit 2.11, Gson, OkHttp Logging, AndroidX, Material, RecyclerView, Gradle 8.9 / AGP 8.7.3, minSdk 24, targetSdk 34 |

---

## Estructura del repositorio

```
.
├── apirestdemo/            # Backend Spring Boot
│   ├── db/                 # Scripts SQL (script.sql, local-user.sql)
│   ├── http/               # Peticiones de ejemplo (requests.http)
│   ├── postman/            # Colección Postman
│   ├── scripts/            # Scripts de arranque/parada de MariaDB (Windows)
│   └── src/                # Código fuente
├── ApirestDemoAndroid/     # App Android (Android Studio)
│   └── app/src/main/       # Código y recursos de la app
└── README.md
```

---

## Requisitos previos

- **JDK 17 o superior** (el proyecto compila con Java 17).
- **MariaDB** (o MySQL) corriendo en `localhost:3306`.
- **Android Studio** (para la app) con un **emulador** o dispositivo.
- Opcional: **IntelliJ IDEA** / **Postman** para el backend.

---

## 1) Backend (`apirestdemo`)

### 1.1 Base de datos

1. Inicia MariaDB en el puerto `3306`.
2. Crea la base de datos, tablas y datos de prueba ejecutando `apirestdemo/db/script.sql`.
3. Crea el usuario de aplicación y dale permisos ejecutando `apirestdemo/db/local-user.sql`.

- Base de datos: `DBTemp`
- Usuario: `dam235` / `D@m235U35._`

> En `scripts/` hay scripts de arranque/parada para Windows (`start-mariadb.cmd`,
> `stop-mariadb.cmd`). Contienen rutas absolutas del equipo del autor, así que ajústalas a tu
> instalación antes de usarlos. Si no, arranca MariaDB como lo hagas normalmente.

### 1.2 Configuración

La configuración está en `apirestdemo/src/main/resources/application.properties`. Todos los valores
tienen un valor por defecto y se pueden sobrescribir con **variables de entorno** (útil para no
dejar credenciales en el código):

| Variable de entorno | Valor por defecto | Descripción |
|---------------------|-------------------|-------------|
| `SERVER_PORT` | `8085` | Puerto del servidor |
| `SERVER_CONTEXT_PATH` | `/api/v1/demoapirestdam235` | Context-path de la API |
| `JWT_SECRET` | `CPqiMDqBnNEXawGoFX7g` | Clave de firma de los JWT |
| `JWT_EXPIRE_MS` | `1800000` | Vigencia del token (30 min) |
| `DB_URL` | `jdbc:mariadb://localhost:3306/DBTemp` | URL de conexión a la base |
| `DB_USER` | `dam235` | Usuario de la base de datos |
| `DB_PASSWORD` | `D@m235U35._` | Contraseña de la base de datos |

Ejemplos:

```properties
# Opción 1: editar directamente application.properties
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASEÑA
```

```powershell
# Opción 2: variables de entorno (PowerShell)
$env:DB_USER="TU_USUARIO"
$env:DB_PASSWORD="TU_CONTRASEÑA"
```

```bash
# Opción 2: variables de entorno (bash)
export DB_USER="TU_USUARIO"
export DB_PASSWORD="TU_CONTRASEÑA"
```

### 1.3 Ejecutar el backend

> URL base de la API: `http://localhost:8085/api/v1/demoapirestdam235`

```bash
# Opción A: Maven Wrapper (Windows)
.\mvnw.cmd spring-boot:run

# Opción B: Maven Wrapper (Linux/Mac)
./mvnw spring-boot:run

# Opción C: desde IntelliJ IDEA, ejecutar ApirestdemoApplication
```

### 1.4 Endpoints

Todas las rutas van **después** del context-path
(`http://localhost:8085/api/v1/demoapirestdam235`).

| Método | Ruta | Autenticación | Descripción | Respuestas |
|--------|------|---------------|-------------|------------|
| POST | `/auth/login` | Pública | Inicia sesión | `200` `{token, expireIn, msj}` / `401` |
| POST | `/auth/logout` | Token | Invalida el token actual | `200` |
| POST | `/users/register` | Pública | Crea una cuenta de usuario | `201` UserDTO / `409` (email duplicado) |
| GET | `/products` | Token | Lista todos los productos | `200` ProductDTO[] / `204` (vacío) |
| GET | `/products/{code}` | Token | Obtiene un producto por código | `200` / `404` |
| POST | `/products` | Token | Crea un producto | `201` |
| PUT | `/products/{code}` | Token | Actualiza un producto | `200` / `404` |
| DELETE | `/products/{code}` | Token | Elimina un producto | `204` |

Las rutas protegidas requieren la cabecera `Authorization: Bearer <token>`.

**Cuerpos (JSON):**

```jsonc
// POST /auth/login
{ "user": "mmartinez@temp.com", "pass": "Temp123." }

// POST /users/register
{ "name": "Carlos", "lastName": "Ramirez", "email": "carlos@temp.com", "password": "Clave123" }

// POST /products  y  PUT /products/{code}
{ "name": "Teclado Mecanico", "status": true }
```

> Ejemplos listos en `apirestdemo/http/requests.http` y en la colección de Postman
> `apirestdemo/postman/apirestdemo.postman_collection.json`.

### 1.5 Credenciales de prueba

| Usuario | Contraseña |
|---------|------------|
| `mmartinez@temp.com` | `Temp123.` |

---

## 2) App Android (`ApirestDemoAndroid`)

### 2.1 Configurar la URL de la API

La URL base se define en `ApirestDemoAndroid/gradle.properties` (propiedad `BASE_URL`) y se usa
desde `BuildConfig.BASE_URL`:

```properties
BASE_URL=http://10.0.2.2:8085/api/v1/demoapirestdam235/
```

- **Emulador:** `10.0.2.2` es el `localhost` de la PC (valor por defecto).
- **Dispositivo físico:** usa la IP de la PC en la red local, ej.
  `http://192.168.1.50:8085/api/v1/demoapirestdam235/`.

También puedes sobrescribirla al compilar sin editar archivos:

```bash
.\gradlew.bat -PBASE_URL=http://192.168.1.50:8085/api/v1/demoapirestdam235/
```

> Si usas un dispositivo físico, agrega esa IP en
> `app/src/main/res/xml/network_security_config.xml` para permitir tráfico HTTP en claro.

### 2.2 Ejecutar la app

```bash
# Con Android Studio: abrir ApirestDemoAndroid, iniciar el emulador y presionar Run.

# O por línea de comandos (genera el APK):
.\gradlew.bat assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

### 2.3 Flujo de la app

1. **Login** → valida usuario/contraseña contra `POST /auth/login`.
2. **Productos** → lista los productos de `GET /products`. Desde aquí puedes:
   - Agregar producto (`POST /products`).
   - Tocar un producto para editarlo (`PUT /products/{code}`).
   - Mantener presionado un producto para eliminarlo (`DELETE /products/{code}`).
3. **Registrar usuario** → crea cuentas con `POST /users/register`.
4. **Cerrar sesión** → llama a `POST /auth/logout`, borra el token y vuelve al login.

El token se guarda en `SharedPreferences` (`SessionManager`), por lo que la sesión se mantiene
mientras el token sea válido. Al expirar, la API responde `401` y la app regresa al login.

---

## Actividades evaluadas (guía)

| # | Actividad | % | Estado |
|---|-----------|:-:|--------|
| 1 | App Android con Retrofit + Gson: login + consulta de productos | 40% | ✅ |
| 2 | Endpoint de logout | 10% | ✅ |
| 3 | Endpoint de creación de cuentas de usuario | 10% | ✅ |
| 4 | Endpoints CRUD de persistencia de Productos | 40% | ✅ |

---

## Notas

- Las credenciales incluidas (`dam235`, `root`) son de **uso local de desarrollo** (MariaDB en
  `localhost`) y provienen de la guía. En producción, defínelas por variables de entorno.
- El backend usa **JWT** para proteger los endpoints; el token se invalida en el logout.
- La app permite **HTTP en claro** solo hacia `10.0.2.2` y `localhost` (configurado en
  `network_security_config.xml`).

---

## Anexo A — Script SQL completo (`db/script.sql`)

```sql
CREATE DATABASE IF NOT EXISTS DBTemp;
USE DBTemp;

CREATE TABLE user (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(256) NOT NULL,
  last_name varchar(256) NOT NULL,
  email varchar(128) NOT NULL,
  password varchar(256) NOT NULL,
  active bit(1) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE product (
  code int(11) NOT NULL AUTO_INCREMENT,
  name varchar(255) DEFAULT NULL,
  status bit(1) DEFAULT NULL,
  PRIMARY KEY (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Datos de prueba
INSERT INTO user VALUES
(1,'Marcos','Martinez','mmartinez@temp.com','{bcrypt}$2a$10$uvWNfki.WxQhUD6e51di0uvC9G0DiGly7/PvyLR0Ay0mpFV24YklG',1);
INSERT INTO product VALUES (1,'Mochila backpack Adidas',1),(2,'USB 2.0 Security',1);
```

## Anexo B — Usuario de aplicación (`db/local-user.sql`)

```sql
CREATE USER IF NOT EXISTS 'dam235'@'localhost' IDENTIFIED BY 'D@m235U35._';
CREATE USER IF NOT EXISTS 'dam235'@'%' IDENTIFIED BY 'D@m235U35._';
GRANT ALL PRIVILEGES ON DBTemp.* TO 'dam235'@'localhost';
GRANT ALL PRIVILEGES ON DBTemp.* TO 'dam235'@'%';
FLUSH PRIVILEGES;
```
