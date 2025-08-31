# TaskFlow API

API RESTful para la gestión de proyectos y tareas, construida con Spring Boot. Este proyecto demuestra un flujo de trabajo de desarrollo profesional, incluyendo un sistema de autenticación basado en JWT, pruebas unitarias y de integración, y una arquitectura desacoplada con el patrón DTO.

## Tecnologías Utilizadas

* **Lenguaje:** Java 17
* **Framework:** Spring Boot 3
* **Seguridad:** Spring Security 6, JSON Web Tokens (JWT)
* **Persistencia:** Spring Data JPA / Hibernate
* **Base de Datos:** PostgreSQL (gestionada con Docker)
* **Pruebas:** JUnit 5, Mockito, Testcontainers
* **Gestión de Dependencias:** Maven

## Endpoints de la API

La API se divide en endpoints públicos (autenticación) y protegidos (recursos).

### Endpoints de Autenticación (Públicos)

| Verbo HTTP | Endpoint                   | Descripción                                    |
|------------|----------------------------|------------------------------------------------|
| `POST`     | `/api/v1/auth/register`    | Registra un nuevo usuario.                     |
| `POST`     | `/api/v1/auth/login`       | Autentica un usuario y devuelve un token JWT.  |

### Endpoints de Proyectos y Tareas (Protegidos - Requieren Bearer Token)

| Verbo HTTP | Endpoint                        | Descripción                                    |
|------------|---------------------------------|------------------------------------------------|
| `GET`      | `/api/v1/projects`              | Obtiene una lista paginada de todos los proyectos. |
| `POST`     | `/api/v1/projects`              | Crea un nuevo proyecto.                        |
| `GET`      | `/api/v1/projects/{id}`         | Obtiene un proyecto específico por su ID.      |
| `PUT`      | `/api/v1/projects/{id}`         | Actualiza un proyecto existente.               |
| `DELETE`   | `/api/v1/projects/{id}`         | Elimina un proyecto.                           |
| `POST`     | `/api/v1/projects/{projectId}/tasks` | Crea una nueva tarea dentro de un proyecto.    |
| `GET`      | `/api/v1/tasks/{id}`            | Obtiene una tarea específica por su ID.        |
| *... (y los otros endpoints de tareas)* |                                         |

## Configuración del Entorno

Este proyecto utiliza archivos de configuración locales tanto para Spring Boot como para Docker Compose. **Estos archivos no deben ser subidos al repositorio.**

1.  **Configuración de Docker (`.env`):**
    * En la raíz del proyecto, copia el archivo `.env.example` y renómbralo a `.env`.
    * Revisa y completa los valores (como `POSTGRES_PASSWORD`) en tu nuevo archivo `.env`.

2.  **Configuración de la Aplicación (`application-dev.properties`):**
    * En `src/main/resources`, copia `application-example.properties` y renómbralo a `application-dev.properties`.
    * Asegúrate de que los valores en este archivo coincidan con los que pusiste en tu archivo `.env`.


## Cómo Ejecutar el Proyecto Localmente

1.  **Requisitos Previos:**
    * Docker y Docker Compose
    * Java 17 (o superior) y Maven

2.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/JuancaSterba/taskflow-api.git](https://github.com/JuancaSterba/taskflow-api.git)
    cd taskflow-api
    ```

3.  **Configuración de Entorno:**
    * En `src/main/resources`, copia `application-example.properties` y renómbralo a `application-dev.properties`.
    * Rellena los valores que faltan (usuario, contraseña, etc.) en tu nuevo archivo `application-dev.properties`.

4.  **Levantar la base de datos con Docker Compose:**
    ```bash
    docker-compose up -d
    ```

5.  **Ejecutar la aplicación Spring Boot:**
    Desde tu IDE o con Maven:
    ```bash
    ./mvnw spring-boot:run
    ```

## Cómo Probar los Endpoints Protegidos

1.  Usa el endpoint `POST /api/v1/auth/register` para crear un usuario.
2.  Usa el endpoint `POST /api/v1/auth/login` con las credenciales de ese usuario para obtener un token JWT.
3.  Copia el token.
4.  Para cualquier petición a un endpoint protegido (ej: `GET /api/v1/projects`), ve a la pestaña **Authorization** en Postman, selecciona **Bearer Token** y pega el token.