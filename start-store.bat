@echo off
setlocal
title Apna Store - Royal Couture E-Commerce Platform
echo ============================================================
echo               APNA STORE - ROYAL COUTURE                    
echo        Full-Stack Spring Boot Platform Launcher             
echo ============================================================
echo.

:: Detect Java
where java >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [WARNING] Java runtime was not detected in PATH.
    echo Starting lightweight PowerShell server on port 3000 as frontend-only fallback...
    powershell -ExecutionPolicy Bypass -File "%~dp0server.ps1"
    goto end
)

echo [*] Starting Apna Store Spring Boot 3 Backend on port 8080...
echo [*] Launching Maven Wrapper (mvnw.cmd spring-boot:run)...
start "Apna Store Spring Boot Server (Port 8080)" cmd /k "cd /d "%~dp0backend" && mvnw.cmd spring-boot:run"

:: Wait for Spring Boot to bootstrap
echo [*] Waiting for server to initialize...
timeout /t 5 /nobreak >nul

echo [*] Launching Apna Store in default browser...
start http://localhost:8080/

echo.
echo ============================================================
echo  Store UI URL      : http://localhost:8080/
echo  Products REST API : http://localhost:8080/api/products
echo  Orders REST API   : http://localhost:8080/api/orders
echo.
echo  Fallback Option   : Run server.ps1 for frontend-only on port 3000
echo  To STOP server    : Double-click stop-store.bat
echo ============================================================
echo.

:end
pause