/* Script para inicializar datos de prueba en MongoDB
   Ejecutar con: mongosh < init-data.js
*/

// Cambiar a la base de datos de catálogo
use("catalog-restaurants");

// Limpiar colecciones existentes
db.restaurantes.deleteMany({});
db.productos.deleteMany({});

// Insertar restaurantes de ejemplo
db.restaurantes.insertMany([
  {
    _id: ObjectId(),
    nombre: "Pizzería Roma",
    descripcion: "Auténtica pizza italiana con horno de leña",
    direccion: "Calle Principal 123, Apt 4",
    latitud: 40.7128,
    longitud: -74.0060,
    telefono: "555-0101",
    email: "info@pizzeriaroma.com",
    horaApertura: "11:00:00",
    horaCierre: "23:00:00",
    calificacion: 4.5,
    numeroResenas: 150,
    imagen: "https://example.com/pizzeria-roma.jpg",
    categorias: ["Italiana", "Pizza", "Comida Rápida"],
    menu: [
      {
        nombre: "Pizza Margherita",
        descripcion: "Pizza clásica con tomate, mozzarella y albahaca",
        precio: 10.99,
        disponible: true,
        categoria: "Pizza",
        tiempoPreparacion: 20
      },
      {
        nombre: "Pizza Pepperoni",
        descripcion: "Pizza con pepperoni y queso derretido",
        precio: 12.50,
        disponible: true,
        categoria: "Pizza",
        tiempoPreparacion: 22
      }
    ],
    activo: true
  },
  {
    _id: ObjectId(),
    nombre: "Comida China Express",
    descripcion: "Auténtica comida china, entrega rápida",
    direccion: "Avenida Comercial 456",
    latitud: 40.7180,
    longitud: -74.0020,
    telefono: "555-0202",
    email: "info@chinaexpress.com",
    horaApertura: "11:30:00",
    horaCierre: "22:30:00",
    calificacion: 4.3,
    numeroResenas: 120,
    imagen: "https://example.com/china-express.jpg",
    categorias: ["China", "Asiática", "Comida Rápida"],
    menu: [
      {
        nombre: "Arroz Frito con Pollo",
        descripcion: "Arroz frito con trozos de pollo y verduras",
        precio: 9.50,
        disponible: true,
        categoria: "Arroces",
        tiempoPreparacion: 15
      }
    ],
    activo: true
  },
  {
    _id: ObjectId(),
    nombre: "Burger House",
    descripcion: "Hamburguesas artesanales y comida americana",
    direccion: "Calle Madison 789",
    latitud: 40.7140,
    longitud: -74.0080,
    telefono: "555-0303",
    email: "info@burgerhouse.com",
    horaApertura: "10:00:00",
    horaCierre: "00:00:00",
    calificacion: 4.2,
    numeroResenas: 200,
    imagen: "https://example.com/burger-house.jpg",
    categorias: ["Americana", "Hamburguesas", "Comida Rápida"],
    menu: [
      {
        nombre: "Burger Clásica",
        descripcion: "Hamburguesa con queso cheddar, lechuga y tomate",
        precio: 11.99,
        disponible: true,
        categoria: "Hamburguesas",
        tiempoPreparacion: 10
      }
    ],
    activo: true
  },
  {
    _id: ObjectId(),
    nombre: "Sushi Paradise",
    descripcion: "Sushi fresco y auténtico preparado diariamente",
    direccion: "Boulevard Central 321",
    latitud: 40.7200,
    longitud: -74.0100,
    telefono: "555-0404",
    email: "info@sushiparadise.com",
    horaApertura: "12:00:00",
    horaCierre: "22:00:00",
    calificacion: 4.8,
    numeroResenas: 300,
    imagen: "https://example.com/sushi-paradise.jpg",
    categorias: ["Japonesa", "Sushi", "Asiática"],
    menu: [
      {
        nombre: "Sushi Roll California",
        descripcion: "Rollos de sushi con camarón, aguacate y pepino",
        precio: 14.99,
        disponible: true,
        categoria: "Sushi",
        tiempoPreparacion: 12
      }
    ],
    activo: true
  }
]);

// Crear índices para mejorar performance
db.restaurantes.createIndex({ nombre: 1 });
db.restaurantes.createIndex({ categorias: 1 });
db.restaurantes.createIndex({ activo: 1 });
db.restaurantes.createIndex({ email: 1 }, { unique: true });
db.restaurantes.createIndex({ latitud: 1, longitud: 1 });

console.log("✓ Datos iniciales insertados correctamente");
console.log("✓ Índices creados");
