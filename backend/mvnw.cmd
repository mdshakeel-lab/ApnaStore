@echo off
@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper Startup Batch Script for Windows
@REM ----------------------------------------------------------------------------
setlocal
set ERROR_CODE=0

set "MAVEN_PROJECTBASEDIR=%~dp0"
if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set "MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%"

set "WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set "WRAPPER_VERSION=3.2.0"
set "WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/%WRAPPER_VERSION%/maven-wrapper-%WRAPPER_VERSION%.jar"

@REM Locate Java
if defined JAVA_HOME (
    set "JAVACMD=%JAVA_HOME%\bin\java.exe"
) else (
    set "JAVACMD=java.exe"
)

@REM If wrapper jar does not exist, download it via PowerShell
if not exist "%WRAPPER_JAR%" (
    echo [*] Downloading Maven Wrapper JAR...
    if not exist "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper" (
        mkdir "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper" >nul 2>&1
    )
    powershell -NoProfile -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object System.Net.WebClient).DownloadFile('%WRAPPER_URL%', '%WRAPPER_JAR%')"
    if errorlevel 1 (
        echo [ERROR] Failed to download maven-wrapper.jar.
        echo Attempting to fall back to system 'mvn'...
        mvn %*
        goto end
    )
)

"%JAVACMD%" -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" -cp "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
if errorlevel 1 set ERROR_CODE=%ERRORLEVEL%

:end
exit /b %ERROR_CODE%
