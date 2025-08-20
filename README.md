# TaskFlow API

API RESTful para la gestión de proyectos y tareas, construida con Spring Boot. Este proyecto forma parte de un proceso de mentoría para afianzar habilidades de desarrollo backend en el ecosistema de Java.

## Tecnologías Utilizadas

* **Lenguaje:** Java 17
* **Framework:** Spring Boot 3
* **Persistencia:** Spring Data JPA / Hibernate
* **Base de Datos:** PostgreSQL (gestionada con Docker)
* **Gestión de Dependencias:** Maven
* **Pruebas de API:** Postman

## Endpoints de la API

Actualmente, la API expone los siguientes endpoints para la gestión de Tareas:

| Verbo HTTP | Endpoint                   | Descripción                                    |
|------------|----------------------------|------------------------------------------------|
| `GET`      | `/api/v1/tasks`            | Obtiene una lista de todas las tareas.         |
| `POST`     | `/api/v1/tasks`            | Crea una nueva tarea.                          |
| `GET`      | `/api/v1/tasks/{id}`       | Obtiene una tarea específica por su ID.        |
| `PUT`      | `/api/v1/tasks/{id}`       | Actualiza una tarea existente.                 |
| `DELETE`   | `/api/v1/tasks/{id}`       | Elimina una tarea.                             |

## Cómo Ejecutar el Proyecto Localmente

Para levantar el proyecto en un entorno local, sigue estos pasos:

1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/JuancaSterba/taskflow-api.git](https://github.com/JuancaSterba/taskflow-api.git)
    cd taskflow-api
    ```

2.  **Levantar la base de datos con Docker:**
    Asegúrate de tener Docker Desktop instalado y corriendo.
    ```bash
    docker run --name taskflow-db -e POSTGRES_USER=user -e POSTGRES_PASSWORD=password -e POSTGRES_DB=taskflow -e TZ="America/Buenos_Aires" -p 5432:5432 -d postgres:16-bookworm
    ```

3.  **Ejecutar la aplicación Spring Boot:**
    Puedes ejecutar la aplicación directamente desde tu IDE (IntelliJ IDEA) o usando Maven en la terminal:
    ```bash
    ./mvnw spring-boot:run
    ```

La API estará disponible en `http://localhost:8080`.