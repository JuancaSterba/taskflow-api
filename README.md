# TaskFlow API

API RESTful para la gestión de proyectos y tareas, construida con Spring Boot. Este proyecto forma parte de un proceso de mentoría para afianzar habilidades de desarrollo backend en el ecosistema de Java.

## Tecnologías Utilizadas

* **Lenguaje:** Java 17
* **Framework:** Spring Boot 3
* **Persistencia:** Spring Data JPA / Hibernate
* **Base de Datos:** PostgreSQL
* **Orquestación de Contenedores:** Docker Compose
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

El entorno de desarrollo está completamente gestionado por Docker Compose, simplificando el proceso de arranque.

1.  **Requisitos Previos:**
    * Tener Docker y Docker Compose instalados.
    * Tener Java 17 (o superior) y Maven instalados.

2.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/JuancaSterba/taskflow-api.git](https://github.com/JuancaSterba/taskflow-api.git)
    cd taskflow-api
    ```

3.  **Levantar el entorno con Docker Compose:**
    Este comando iniciará la base de datos PostgreSQL en segundo plano.
    ```bash
    docker-compose up -d
    ```

4.  **Ejecutar la aplicación Spring Boot:**
    Puedes ejecutar la aplicación directamente desde tu IDE (IntelliJ IDEA) o usando Maven en la terminal:
    ```bash
    ./mvnw spring-boot:run
    ```

La API estará disponible en `http://localhost:8080`.

## Gestión del Entorno

* **Para detener el entorno:** `docker-compose down`
* **Para ver los logs de la base de datos:** `docker-compose logs -f postgres-db`