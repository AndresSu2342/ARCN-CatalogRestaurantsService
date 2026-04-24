# Microservicio de Catálogo de Restaurantes - ARCN

## Descripción

Implementación del **Bounded Context "Catálogo de Restaurantes"** del dominio ARCN (Architecture Restaurant Catalog Navigation), utilizando principios de **Domain-Driven Design (DDD)** y **SOLID**.

Este microservicio gestiona el catálogo de restaurantes, permitiendo:
- Buscar restaurantes cercanos por ubicación geográfica
- Consultar información de restaurantes
- Gestionar menús de productos
- Filtrar por categoría, calificación y horario de apertura

## Arquitectura y Estructura

### Bounded Context: Catálogo de Restaurantes

El proyecto sigue una estructura modular con separación clara de responsabilidades:

```
src/main/java/arcn/catalog/
├── CatalogRestaurantsApplication.java    # Clase principal
├── config/                                # Configuración
│   └── MongoConfig.java                  # Configuración de MongoDB
├── model/                                 # Modelo de Dominio
│   ├── Restaurante.java                  # Agregado Raíz
│   ├── Producto.java                     # Entidad del Menú
│   └── BusquedaCriterios.java            # Objeto Valor
├── dto/                                   # Data Transfer Objects
│   ├── RestauranteDTO.java
│   ├── ProductoDTO.java
│   └── BusquedaRequestDTO.java
├── repository/                            # Capa de Persistencia
│   └── RestauranteRepository.java        # Repositorio MongoDB
├── service/                               # Lógica de Negocio
│   ├── RestauranteService.java           # Interfaz del Servicio
│   ├── RestauranteServiceImpl.java        # Implementación
│   └── MapperService.java                # Conversión DTO ↔ Entidades
├── controller/                            # Capa de Presentación
│   └── RestauranteController.java        # Endpoints REST
└── exception/                             # Manejo de Excepciones
    ├── RestauranteNoEncontradoException.java
    ├── ErrorResponse.java
    └── GlobalExceptionHandler.java
```

## Requisitos

- **Java 17+**
- **Maven 3.8+**
- **MongoDB 5.0+** (local o remoto)
- **Spring Boot 3.0.0**

## Configuración de MongoDB

### Opción 1: MongoDB Local

1. **Instalar MongoDB Community Edition** desde https://www.mongodb.com/try/download/community

2. **Iniciar el servicio MongoDB**:
   ```bash
   # Windows (si está en PATH)
   mongod
   
   # O especificar la ruta
   "C:\Program Files\MongoDB\Server\5.0\bin\mongod.exe"
   ```

3. **Verificar conexión**:
   ```bash
   mongo mongodb://localhost:27017/catalog-restaurants
   ```

### Opción 2: MongoDB Atlas (Cloud)

1. Crear una cuenta en https://www.mongodb.com/cloud/atlas

2. Actualizar `application.properties`:
   ```properties
   spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/catalog-restaurants
   ```

### Opción 3: MongoDB con Docker

```bash
docker run -d \
  --name mongodb \
  -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=password \
  mongo:5.0
```

Luego actualizar:
```properties
spring.data.mongodb.uri=mongodb://admin:password@localhost:27017/catalog-restaurants?authSource=admin
```

## Instalación y Ejecución

### Compilar el proyecto

```bash
# Con Maven Wrapper
./mvnw clean compile

# O con Maven
mvn clean compile
```

### Ejecutar pruebas

```bash
./mvnw clean test
```

### Ejecutar la aplicación

```bash
# Opción 1: Con Maven
./mvnw spring-boot:run

# Opción 2: Compilar y ejecutar JAR
./mvnw clean package
java -jar target/catalog-restaurants-service-1.0.0.jar
```

La aplicación estará disponible en: **http://localhost:8081/**

## Endpoints de la API

### 1. Buscar Restaurantes Cercanos
```http
POST /api/v1/restaurantes/buscar
Content-Type: application/json

{
  "latitudUsuario": 40.7128,
  "longitudUsuario": -74.0060,
  "radioKm": 5.0,
  "hora": "12:00:00",
  "categoria": "Italiana",
  "calificacionMinima": 4.0
}
```

**Respuesta (200 OK)**:
```json
[
  {
    "id": "507f1f77bcf86cd799439011",
    "nombre": "Restaurante Italia",
    "direccion": "Calle Principal 123",
    "latitud": 40.7180,
    "longitud": -74.0020,
    "horaApertura": "11:00:00",
    "horaCierre": "22:00:00",
    "calificacion": 4.5,
    "distanciaKm": 0.85,
    "activo": true
  }
]
```

### 2. Obtener Todos los Restaurantes
```http
GET /api/v1/restaurantes
```

### 3. Obtener Restaurante por ID
```http
GET /api/v1/restaurantes/{id}
```

### 4. Crear Restaurante
```http
POST /api/v1/restaurantes
Content-Type: application/json

{
  "nombre": "Mi Restaurante",
  "descripcion": "Excelente comida italiana",
  "direccion": "Calle Principal 456",
  "latitud": 40.7128,
  "longitud": -74.0060,
  "telefono": "555-1234",
  "email": "info@mirestaurante.com",
  "horaApertura": "11:00:00",
  "horaCierre": "23:00:00",
  "categorias": ["Italiana", "Pasta"],
  "activo": true
}
```

### 5. Actualizar Restaurante
```http
PUT /api/v1/restaurantes/{id}
Content-Type: application/json

{
  "nombre": "Mi Restaurante Actualizado",
  ...
}
```

### 6. Desactivar Restaurante
```http
DELETE /api/v1/restaurantes/{id}
```

### 7. Obtener Menú del Restaurante
```http
GET /api/v1/restaurantes/{id}/menu
```

**Respuesta (200 OK)**:
```json
[
  {
    "id": "507f1f77bcf86cd799439012",
    "nombre": "Pasta Carbonara",
    "descripcion": "Pasta fresca con salsa carbonara",
    "precio": 12.50,
    "disponible": true,
    "categoria": "Pasta",
    "tiempoPreparacion": 15
  }
]
```

### 8. Agregar Producto al Menú
```http
POST /api/v1/restaurantes/{id}/menu
Content-Type: application/json

{
  "nombre": "Pizza Margherita",
  "descripcion": "Pizza clásica con tomate y mozzarella",
  "precio": 10.99,
  "disponible": true,
  "categoria": "Pizza",
  "tiempoPreparacion": 20,
  "imagen": "https://example.com/pizza.jpg"
}
```

### 9. Buscar por Nombre
```http
GET /api/v1/restaurantes/nombre/{nombre}
```

### 10. Buscar por Categoría
```http
GET /api/v1/restaurantes/categoria/{categoria}
```

## Casos de Uso Implementados

### 1. **Buscar Restaurantes Cercanos**
- **Actor**: Usuario
- **Precondiciones**: Usuario tiene ubicación geográfica
- **Flujo Principal**:
  1. Usuario proporciona coordenadas y radio de búsqueda
  2. Sistema calcula distancia usando fórmula Haversine
  3. Filtra restaurantes activos dentro del radio
  4. Aplica filtros adicionales (horario, categoría, calificación)
  5. Retorna restaurantes ordenados por distancia

### 2. **Filtrar por Ubicación y Horario**
- Sistema verifica si restaurante está abierto a la hora solicitada
- Calcula distancia geográfica en km

### 3. **Gestionar Catálogo de Restaurantes**
- CRUD completo de restaurantes
- Borrado lógico (desactivación)

### 4. **Gestionar Menú de Productos**
- Agregar productos al menú
- Consultar menú completo del restaurante

## Validaciones y Reglas de Negocio

### Restaurante
- Nombre: Requerido, no vacío
- Dirección: Requerida, no vacía
- Coordenadas: Latitud y longitud válidas
- Horarios: Hora de apertura < hora de cierre
- Email: Único en el sistema

### Producto
- Nombre: Requerido, no vacío
- Precio: Mayor a 0
- Categoría: No vacía

## Manejo de Errores

La API retorna respuestas estándar con código HTTP y mensaje de error:

```json
{
  "status": 400,
  "mensaje": "Error de validación",
  "detalle": "nombre: El nombre del restaurante es requerido",
  "timestamp": "2024-04-23T10:30:00",
  "ruta": "/api/v1/restaurantes"
}
```

### Códigos HTTP Utilizados
- **200 OK**: Solicitud exitosa
- **201 Created**: Recurso creado
- **204 No Content**: Operación exitosa sin contenido
- **400 Bad Request**: Datos inválidos
- **404 Not Found**: Recurso no encontrado
- **500 Internal Server Error**: Error del servidor

## Testing

### Ejecutar todas las pruebas
```bash
./mvnw clean test
```

### Generar reporte de cobertura
```bash
./mvnw clean verify
```

El reporte se genera en: `target/site/jacoco/index.html`

**Cobertura mínima requerida**: 80%

## Ejemplo de Uso con cURL

```bash
# 1. Crear un restaurante
curl -X POST http://localhost:8081/api/v1/restaurantes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Pizzería Roma",
    "descripcion": "Auténtica pizza italiana",
    "direccion": "Via Roma 123",
    "latitud": 40.7128,
    "longitud": -74.0060,
    "telefono": "555-0101",
    "email": "info@pizzeriaroma.com",
    "horaApertura": "11:00:00",
    "horaCierre": "23:00:00",
    "categorias": ["Italiana", "Pizza"],
    "activo": true
  }'

# 2. Buscar restaurantes cercanos
curl -X POST http://localhost:8081/api/v1/restaurantes/buscar \
  -H "Content-Type: application/json" \
  -d '{
    "latitudUsuario": 40.7128,
    "longitudUsuario": -74.0060,
    "radioKm": 5.0,
    "categoria": "Pizza"
  }'

# 3. Obtener restaurantes
curl http://localhost:8081/api/v1/restaurantes
```

## Principios SOLID Aplicados

- **Single Responsibility**: Cada clase tiene una única responsabilidad
- **Open/Closed**: Abierto para extensión, cerrado para modificación
- **Liskov Substitution**: Implementación correcta de interfaces
- **Interface Segregation**: Interfaces específicas y cohesivas
- **Dependency Inversion**: Inyección de dependencias con Spring

## Tecnologías Utilizadas

- **Spring Boot 3.0**: Framework web
- **Spring Data MongoDB**: ORM/ODM
- **Lombok**: Reducción de boilerplate
- **Jakarta Validation**: Validación de datos
- **JUnit 5**: Testing
- **Mockito**: Mocking en tests
- **Maven**: Build tool
- **MongoDB**: Base de datos NoSQL

## Variables de Entorno (Opcional)

```bash
# Configurar URI de MongoDB diferente
export SPRING_DATA_MONGODB_URI=mongodb://user:pass@host:port/database

# Configurar puerto de la aplicación
export SERVER_PORT=8081
```

## Logs

Los logs se escriben en la consola con el siguiente formato:

```
2024-04-23 10:30:45 - Buscando restaurantes cercanos - Lat: 40.7128, Lon: -74.0060, Radio: 5 km
```

Nivel de log para el paquete: `DEBUG`

## Próximos Pasos

1. **Implementar eventos de dominio**
2. **Agregar caché distribuido**
3. **Implementar paginación en búsquedas**
4. **Agregar autenticación y autorización**
5. **Documentación con Swagger/OpenAPI**
6. **Integración con otros Bounded Contexts**

## Autor

ARCN - Architecture Restaurant Catalog Navigation

## Licencia

Bajo licencia interna de ARCN
