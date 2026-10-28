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

echo [2/2] Iniciando AeroGestion (Spring Boot)...
echo       Se abrira http://localhost:8080 en tu navegador en unos segundos.
echo       Para detener: cierra esta ventana y la de MongoDB.
start "" cmd /c "timeout /t 25 /nobreak >nul & start http://localhost:8080"

cd /d "%~dp0backend"
call "%TOOLS%\apache-maven-3.9.9\bin\mvn.cmd" -q spring-boot:run
pause
