#!/bin/bash

# Script para inicializar MongoDB y cargar datos de ejemplo
# Uso: bash setup-mongodb.sh

echo "=========================================="
echo "Inicialización de MongoDB - ARCN Catalog"
echo "=========================================="

# Verificar si MongoDB está instalado
if ! command -v mongod &> /dev/null; then
    echo "❌ MongoDB no está instalado"
    echo "Instalar desde: https://www.mongodb.com/try/download/community"
    exit 1
fi

# Verificar si mongosh está instalado
if ! command -v mongosh &> /dev/null; then
    echo "⚠️  mongosh no está disponible, usando mongo"
    MONGO_CLIENT="mongo"
else
    MONGO_CLIENT="mongosh"
fi

echo "✓ MongoDB encontrado"

# Verificar si el servicio MongoDB está corriendo
if ! pgrep -x "mongod" > /dev/null; then
    echo "📝 Iniciando servicio MongoDB..."
    # En background
    mongod --dbpath ./data/db > ./logs/mongodb.log 2>&1 &
    echo "   PID: $!"
    
    # Esperar a que MongoDB inicie
    echo "⏳ Esperando a que MongoDB inicie..."
    sleep 3
else
    echo "✓ MongoDB ya está en ejecución"
fi

# Verificar conexión
echo "🔗 Verificando conexión a MongoDB..."
echo "use catalog-restaurants;" | $MONGO_CLIENT 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✓ Conexión establecida"
    
    echo "📥 Cargando datos iniciales..."
    $MONGO_CLIENT < init-data.js
    
    if [ $? -eq 0 ]; then
        echo "✅ Setup completado exitosamente!"
        echo ""
        echo "Conexión: mongodb://localhost:27017/catalog-restaurants"
        echo "Base de datos: catalog-restaurants"
    else
        echo "❌ Error al cargar datos"
        exit 1
    fi
else
    echo "❌ No se puede conectar a MongoDB"
    echo "Asegúrese de que MongoDB está corriendo en localhost:27017"
    exit 1
fi

echo "=========================================="
