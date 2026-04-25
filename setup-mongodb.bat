@echo off
REM Script para inicializar MongoDB y cargar datos de ejemplo
REM Uso: setup-mongodb.bat

echo.
echo ==========================================
echo Inicializacion de MongoDB - ARCN Catalog
echo ==========================================
echo.

REM Verificar si MongoDB está en PATH
where mongod >nul 2>nul
if errorlevel 1 (
    echo [ERROR] MongoDB no esta instalado o no esta en PATH
    echo Instalar desde: https://www.mongodb.com/try/download/community
    exit /b 1
)

echo [OK] MongoDB encontrado

REM Crear directorio para datos si no existe
if not exist "data\db" (
    echo [INFO] Creando directorio data\db...
    mkdir data\db
)

if not exist "logs" (
    mkdir logs
)

REM Verificar si MongoDB está corriendo
tasklist | find /i "mongod.exe" >nul 2>nul
if errorlevel 1 (
    echo [INFO] Iniciando servicio MongoDB...
    start mongod --dbpath ./data/db --logpath ./logs/mongodb.log
    timeout /t 3 /nobreak
) else (
    echo [OK] MongoDB ya esta en ejecucion
)

REM Verificar conexión
echo [INFO] Verificando conexion a MongoDB...
echo use catalog-restaurants; | mongosh >nul 2>&1

if errorlevel 0 (
    echo [OK] Conexion establecida
    
    echo [INFO] Cargando datos iniciales...
    mongosh < init-data.js
    
    if errorlevel 0 (
        echo.
        echo [OK] ==========================================
        echo [OK] Setup completado exitosamente!
        echo [OK] ==========================================
        echo.
        echo Conexion: mongodb://localhost:27017/catalog-restaurants
        echo Base de datos: catalog-restaurants
        echo.
    ) else (
        echo [ERROR] Error al cargar datos
        exit /b 1
    )
) else (
    echo [ERROR] No se puede conectar a MongoDB
    echo Asegurese de que MongoDB esta corriendo en localhost:27017
    exit /b 1
)

pause
