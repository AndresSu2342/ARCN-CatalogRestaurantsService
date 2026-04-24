# Arquitectura del Microservicio - Catálogo de Restaurantes

## 1. Patrón Domain-Driven Design (DDD)

### 1.1 Bounded Context
El **Bounded Context** "Catálogo de Restaurantes" encapsula:
- Gestión de información de restaurantes
- Búsqueda geolocalizada
- Administración de menús
- Criterios de filtrado (categoría, calificación, horarios)

### 1.2 Agregados

#### Agregado Raíz: Restaurante
```
Restaurante (Agregado Raíz)
├── id (Identity)
├── nombre
├── descripcion
├── ubicacion (Latitud, Longitud)
├── horarios (Apertura, Cierre)
├── calificacion
├── Menu (Colección de Productos)
└── estado (activo/inactivo)
```

**Responsabilidades:**
- Validar coherencia de datos internos
- Calcular distancia a ubicación del usuario
- Verificar si está abierto en un horario dado
- Gestionar el menú de productos

#### Entidad: Producto
```
Producto (Entidad dentro del Agregado)
├── id
├── nombre
├── descripcion
├── precio
├── disponibilidad
├── categoria
├── tiempoPreparacion
└── imagen
```

**Responsabilidades:**
- Representar items del menú
- Validar datos del producto

#### Objeto Valor: BusquedaCriterios
```
BusquedaCriterios (Value Object)
├── latitudUsuario
├── longitudUsuario
├── radioKm
├── hora
├── categoria
└── calificacionMinima
```

**Responsabilidades:**
- Encapsular criterios de búsqueda
- Validar coherencia de criterios

### 1.3 Repositorio

```java
RestauranteRepository (Repository)
├── CRUD básico
├── findByNombreContainingIgnoreCase(nombre)
├── findByCategoriasContaining(categoria)
├── findAbiertosEnHora(hora)
├── findByCalificacionMinima(calificacion)
└── findActivosByCategoria(categoria)
```

**Responsabilidades:**
- Persistencia en MongoDB
- Consultas especializadas del dominio

### 1.4 Servicios de Dominio

#### RestauranteService (Domain Service)
Coordina la lógica de negocio del Bounded Context:
- Buscar restaurantes cercanos (Cálculo de distancias)
- Gestión del ciclo de vida (CRUD)
- Filtrado por múltiples criterios
- Validaciones de negocio

#### MapperService (Application Service)
Conversión entre:
- Entidades de Dominio → DTOs
- DTOs → Entidades de Dominio

## 2. Capas de la Arquitectura

```
┌─────────────────────────────────────────────────┐
│          CAPA DE PRESENTACIÓN (HTTP)            │
│  RestauranteController                          │
│  - Endpoints REST                               │
│  - Validación de entrada                        │
│  - Serialización/Deserialización                │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│      CAPA DE APLICACIÓN (Servicios)             │
│  RestauranteServiceImpl                          │
│  MapperService                                  │
│  - Orquestación de lógica de negocio            │
│  - Conversión de objetos                        │
│  - Coordinación entre capas                     │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│      CAPA DE DOMINIO (Lógica de Negocio)        │
│  Restaurante (Agregado)                         │
│  Producto (Entidad)                             │
│  BusquedaCriterios (Value Object)               │
│  RestauranteService (Domain Service)            │
│  - Reglas de negocio                            │
│  - Validaciones                                 │
│  - Comportamiento del dominio                   │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│      CAPA DE PERSISTENCIA (Acceso a Datos)      │
│  RestauranteRepository                          │
│  MongoDB Connection                             │
│  - Operaciones CRUD                             │
│  - Consultas especializadas                     │
│  - Mapeo objeto-documento                       │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│          CAPA DE INFRAESTRUCTURA                │
│  MongoDB                                        │
│  Spring Data MongoDB                            │
│  - Almacenamiento persistente                   │
│  - Conexiones de BD                             │
└─────────────────────────────────────────────────┘
```

## 3. Flujo de Solicitud: Buscar Restaurantes Cercanos

```
1. Usuario realiza POST a /api/v1/restaurantes/buscar
   │
   └─> RestauranteController.buscarRestaurantesCercanos()
       │
       ├─> Valida BusquedaRequestDTO
       │
       ├─> Crea BusquedaCriterios
       │
       └─> RestauranteService.buscarRestaurantesCercanos(criterios)
           │
           ├─> Valida criterios
           │
           ├─> RestauranteRepository.findAbiertosEnHora(hora)
           │   (Consulta en MongoDB)
           │
           ├─> Filtra por categoría (en memoria)
           │
           ├─> Filtra por calificación (en memoria)
           │
           ├─> Para cada restaurante:
           │   └─> Restaurante.calcularDistancia(lat, lon)
           │       (Fórmula Haversine)
           │
           ├─> Filtra por radio en km
           │
           ├─> Ordena por distancia
           │
           ├─> MapperService.convertirADTO() por cada resultado
           │
           └─> Retorna List<RestauranteDTO> ordenada

2. RestauranteController serializa respuesta a JSON
   │
   └─> HTTP 200 OK + JSON response
```

## 4. Patrones y Principios Utilizados

### 4.1 Patrones de Diseño

| Patrón | Aplicación |
|--------|-----------|
| **Repository** | Abstracción del acceso a datos |
| **Service Locator** | MapperService para conversiones |
| **Builder** | Construcción de objetos complejos |
| **Value Object** | BusquedaCriterios |
| **Entity** | Restaurante, Producto |
| **Aggregate** | Restaurante con Menu |
| **DTO** | Transferencia de datos |

### 4.2 Principios SOLID

| Principio | Implementación |
|-----------|---------------|
| **S** (Single Responsibility) | Cada clase tiene una única razón para cambiar |
| **O** (Open/Closed) | Abierto para extensión (nuevos criterios de búsqueda) |
| **L** (Liskov Substitution) | Implementación correcta de interfaces |
| **I** (Interface Segregation) | RestauranteService define operaciones específicas |
| **D** (Dependency Inversion) | Inyección de dependencias con @Autowired |

### 4.3 Principios de DDD

| Concepto | Implementación |
|----------|---------------|
| **Bounded Context** | Catálogo de Restaurantes |
| **Agregado** | Restaurante con su menú |
| **Entidad** | Restaurante, Producto |
| **Value Object** | BusquedaCriterios |
| **Repositorio** | RestauranteRepository |
| **Servicio de Dominio** | RestauranteService |

## 5. Decisiones Arquitectónicas

### 5.1 MongoDB como Base de Datos
**Por qué**: 
- Flexibilidad de esquema (Documents)
- Consultas geoespaciales
- Escalabilidad horizontal
- Almacenamiento de arrays (Menu)

### 5.2 Cálculo de Distancia en Aplicación
**Por qué**:
- Fórmula Haversine más precisa
- Filtering en memoria es más eficiente con pocos resultados
- Evita queries complejas en MongoDB

### 5.3 Borrado Lógico en lugar de Físico
**Por qué**:
- Mantiene historico de datos
- Evita problemas de integridad referencial
- Facilita auditoría

### 5.4 DTO Separados del Modelo
**Por qué**:
- Encapsulamiento de la API pública
- Flexibilidad para cambios internos
- Validación específica del endpoint

## 6. Extensibilidad Futura

### 6.1 Agregar Eventos de Dominio
```java
// RestauranteCreado, RestauranteActualizado, etc.
public class RestauranteCreado implements DomainEvent {
    private String restauranteId;
    private LocalDateTime ocurridoEn;
}
```

### 6.2 Agregar Especificaciones
```java
// Para consultas complejas
public class RestauranteCercanoSpec extends Specification<Restaurante> {
    // Query builder
}
```

### 6.3 Caché Distribuido
```java
@Cacheable("restaurantes")
public List<RestauranteDTO> obtenerTodos() { ... }
```

### 6.4 Métricas y Observabilidad
```java
@Timed("restaurante.busqueda")
public List<RestauranteDTO> buscar() { ... }
```

## 7. Referencias

- Eric Evans - "Domain-Driven Design" (Blue Book)
- Vaughn Vernon - "Implementing Domain-Driven Design"
- Spring Data MongoDB Documentation
- Clean Code Architecture by Robert C. Martin
