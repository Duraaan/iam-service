# IAM Service API

Servicio de Identidad, Autenticación y Autorización (IAM) construido con **Spring Boot 3** y **Java 21**. Proporciona autenticación basada en JWT, control de acceso basado en roles (RBAC) y gestión de usuarios con soporte para múltiples perfiles.

## Tecnologías

- Java 21
- Spring Boot 3.5.6
- Spring Security
- Spring Data JPA / Hibernate
- PostgreSQL
- JWT (JJWT 0.12.5)
- Lombok
- Maven

## Características

- Autenticación stateless con tokens JWT
- Jerarquía de 4 roles: `SUPER_ADMIN`, `ADMIN`, `STAFF`, `USER`
- Control de acceso por rol con `@PreAuthorize`
- Eliminación lógica (soft delete) de usuarios
- Perfiles diferenciados por rol
- Auditoría automática (`createdAt`, `updatedAt`)
- Inicialización automática de roles y super-admin al arrancar
- Manejo centralizado de excepciones
- Soporte CORS para frontend en `localhost:3000`

## Requisitos previos

- Java 21
- PostgreSQL corriendo en `localhost:5432`
- Base de datos `iam_bd` creada previamente

## Variables de entorno

Antes de ejecutar, configura las siguientes variables:

| Variable               | Descripción                                   |
|------------------------|-----------------------------------------------|
| `DB_USERNAME`          | Usuario de PostgreSQL                         |
| `DB_PASSWORD`          | Contraseña de PostgreSQL                      |
| `JWT_SECRET_KEY`       | Clave secreta JWT (codificada en Base64)      |
| `SUPER_ADMIN_EMAIL`    | Email del super-admin inicial                 |
| `SUPER_ADMIN_PASSWORD` | Contraseña del super-admin inicial            |

## Instalación y ejecución

```bash
# Clonar el repositorio
git clone <url-del-repositorio>
cd iam-service

# Ejecutar con Maven Wrapper
./mvnw spring-boot:run

# O compilar y ejecutar el JAR
./mvnw clean package
java -jar target/iam-service-0.0.1-SNAPSHOT.jar
```

El servidor inicia en `http://localhost:8080`.

Al arrancar, se crean automáticamente los 4 roles y el usuario `SUPER_ADMIN` si no existen.

## Endpoints

### Autenticación — `/api/v1/auth`

| Método | Ruta                      | Descripción                  | Acceso  |
|--------|---------------------------|------------------------------|---------|
| POST   | `/register-user`          | Registrar usuario regular    | Público |
| POST   | `/login`                  | Iniciar sesión               | Público |

### Administración — `/api/v1/admin`

| Método | Ruta                      | Descripción                  | Rol requerido       |
|--------|---------------------------|------------------------------|---------------------|
| POST   | `/register-admin`         | Crear administrador          | `SUPER_ADMIN`       |
| POST   | `/register-staff`         | Crear miembro de staff       | `ADMIN`, `SUPER_ADMIN` |

### Usuarios — `/api/v1/users`

| Método | Ruta                      | Descripción                  | Rol requerido          |
|--------|---------------------------|------------------------------|------------------------|
| GET    | `/`                       | Listar usuarios activos      | `ADMIN`, `SUPER_ADMIN` |
| GET    | `/user/{id}`              | Obtener usuario por ID       | `ADMIN`, `SUPER_ADMIN` |
| PUT    | `/user/{id}`              | Actualizar usuario           | `ADMIN`, `SUPER_ADMIN` |
| DELETE | `/user/{id}`              | Eliminar usuario (lógico)    | `SUPER_ADMIN`          |

### Pruebas — `/api/v1/test`

| Método | Ruta                      | Rol requerido       |
|--------|---------------------------|---------------------|
| GET    | `/hello-public`           | Ninguno (público)   |
| GET    | `/hello-secured`          | Cualquier autenticado |
| GET    | `/hello-user`             | `USER`              |
| GET    | `/hello-staff`            | `STAFF`             |
| GET    | `/hello-admin`            | `ADMIN`             |
| GET    | `/hello-superadmin`       | `SUPER_ADMIN`       |
| GET    | `/hello-management`       | `ADMIN`, `SUPER_ADMIN` |

## Autenticación

Los endpoints protegidos requieren el token JWT en el header:

```
Authorization: Bearer <token>
```

El token se obtiene al registrarse o al hacer login y tiene una validez de **10 horas**.

## Estructura del proyecto

```
src/main/java/cl/sdc/iam/
├── config/             # Configuración de seguridad y seeder de roles
│   └── filter/         # Filtro JWT
├── controller/         # Controladores REST
├── service/            # Lógica de negocio
├── model/
│   ├── entity/         # Entidades JPA (User, Role, perfiles)
│   └── enums/          # RoleName enum
├── repository/         # Repositorios Spring Data
├── dto/                # DTOs de request/response
└── exception/          # Excepciones personalizadas y handler global
```

## Modelo de datos

```
users ──────── user_roles ──────── roles
  │
  ├── user_profiles    (1:1, solo ROLE_USER)
  ├── admin_profiles   (1:1, solo ROLE_ADMIN)
  └── staff_profiles   (1:1, solo ROLE_STAFF)
```
