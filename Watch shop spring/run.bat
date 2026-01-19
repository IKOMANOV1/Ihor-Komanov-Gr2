@echo off
echo ==========================================
echo       Watch Store - Auto Launcher
echo ==========================================

echo [1/3] Starting/Checking Database (Docker)...
docker-compose up -d
IF %ERRORLEVEL% NEQ 0 (
    echo Error: Docker is not running or docker-compose failed.
    echo Please make sure Docker Desktop is started.
    pause
    exit /b
)

echo [1.5/3] Stopping existing application...
taskkill /F /IM java.exe 2>nul
timeout /t 2 /nobreak >nul

echo.
echo [2/3] Building Project (Skipping Tests)...
call mvnw.cmd clean package -DskipTests
IF %ERRORLEVEL% NEQ 0 (
    echo Error: Maven build failed.
    pause
    exit /b
)

echo.
echo [3/3] Starting Application...
title Watch Store App
copy /Y "src\main\resources\application.yml" "application.yml" >nul
title Watch Store App
copy /Y "src\main\resources\application.yml" "application.yml" >nul
java -jar "target/watch-store-0.0.1-SNAPSHOT.jar" --spring.thymeleaf.prefix=file:src/main/resources/templates/

pause
