# Apna Store Launcher (PowerShell)
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "               APNA STORE - ROYAL COUTURE                   " -ForegroundColor Yellow
Write-Host "       Full-Stack Spring Boot 3 Platform Launcher           " -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

if (Get-Command java -ErrorAction SilentlyContinue) {
    Write-Host "[*] Java runtime detected. Launching Spring Boot server via Maven Wrapper..." -ForegroundColor Green
    Set-Location "$scriptDir\backend"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$scriptDir\backend'; .\mvnw.cmd spring-boot:run"
    Start-Sleep -Seconds 5
    Start-Process "http://localhost:8080/"
} else {
    Write-Host "[!] Java not found. Launching native PowerShell HTTP server on port 3000 as frontend-only fallback..." -ForegroundColor Yellow
    Start-Process powershell -ArgumentList "-NoExit", "-File", "'$scriptDir\server.ps1'"
    Start-Sleep -Seconds 2
    Start-Process "http://localhost:3000/"
}
Write-Host "[*] Application launched successfully!" -ForegroundColor Green