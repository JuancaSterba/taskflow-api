# TaskFlow API

API RESTful para la gestión de proyectos y tareas, construida con Spring Boot. Este proyecto demuestra un flujo de trabajo de desarrollo profesional, incluyendo un sistema de autenticación basado en JWT, pruebas unitarias y de integración, y una arquitectura desacoplada con el patrón DTO.

## Tecnologías Utilizadas

* **Lenguaje:** Java 17
* **Framework:** Spring Boot 3
* **Seguridad:** Spring Security 6, JSON Web Tokens (JWT)
* **Persistencia:** Spring Data JPA / Hibernate
* **Base de Datos:** PostgreSQL (gestionada con Docker)
* **Pruebas:** JUnit 5, Mockito, Testc
* * 
* **Gestión de Dependencias:** Maven

## Arquitectura

Esta API funciona como el servicio central de un ecosistema de microservicios. Implementa una **arquitectura orientada a eventos** para comunicarse con otros servicios de forma desacoplada.

* **Productor de Eventos:** Publica eventos en un topic de Kafka (ej: `tasks-events`) cuando ocurren acciones importantes como la creación de una tarea.

## Documentación de la API (Interactiva)

La documentación completa y detallada de todos los endpoints se genera automáticamente con OpenAPI y puede ser consultada de forma interactiva a través de Swagger UI una vez que la aplicación está corriendo.

* **URL de la UI de Swagger:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **Especificación OpenAPI (JSON):** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

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
    * En la raíz del proyecto, copia el archivo `.env.example` y renómbralo a `.env`. Completa los valores secretos.
    * En `src/main/resources`, copia `application-example.properties` y renómbralo a `application-dev.properties`. Asegúrate de que los valores coincidan con tu archivo `.env`.

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
4.  Para cualquier petición a un endpoint protegido (ej: `GET /api/v1/projects`), ve a la pestaña **Authorization** en Postman (o haz clic en el botón "Authorize" en la UI de Swagger), selecciona **Bearer Token** y pega el token.