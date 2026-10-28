@echo off
title AeroGestion
echo ==========================================
echo   AeroGestion - iniciando el sistema...
echo ==========================================

set TOOLS=C:\Users\Personal\tools
if not exist "%TOOLS%\mongodb-data" mkdir "%TOOLS%\mongodb-data"

echo [1/2] Iniciando MongoDB (puerto 27017)...
start "MongoDB" /min "%TOOLS%\mongodb-win32-x86_64-windows-8.0.4\bin\mongod.exe" --dbpath "%TOOLS%\mongodb-data" --bind_ip 127.0.0.1
timeout /t 4 /nobreak >nul

echo [2/2] Iniciando la API de AeroGestion (Spring Boot)...
echo       Cuando aparezca "Started AeroGestionApplication", la API esta lista en:
echo       http://localhost:8080/api
echo       Pruebala con Postman importando postman\AeroGestion.postman_collection.json
echo       Para detener: cierra esta ventana y la de MongoDB.

cd /d "%~dp0backend"
call "%TOOLS%\apache-maven-3.9.9\bin\mvn.cmd" -q spring-boot:run
pause
