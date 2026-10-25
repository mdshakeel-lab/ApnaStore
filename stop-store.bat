@echo off
setlocal
echo ============================================================
echo           Stopping Apna Store Backend Server...
echo ============================================================
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080') do (
    taskkill /F /PID %%a >nul 2>&1
)
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :3000') do (
    taskkill /F /PID %%a >nul 2>&1
)
echo [*] Apna Store servers on ports 8080 and 3000 stopped.
echo ============================================================
pause