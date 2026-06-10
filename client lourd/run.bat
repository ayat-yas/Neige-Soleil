@echo off
echo ======================================
echo   Neige ^& Soleil — Client Lourd Java
echo ======================================
echo.

java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERREUR : Java introuvable. Installez Java 17+ depuis https://adoptium.net
    pause & exit /b 1
)

if not exist "lib\mysql-connector-j.jar" (
    echo ERREUR : lib\mysql-connector-j.jar introuvable.
    echo Telechargez depuis https://dev.mysql.com/downloads/connector/j/
    pause & exit /b 1
)

echo Compilation...
if not exist "out" mkdir out
javac -cp "lib\mysql-connector-j.jar" -d out -sourcepath src\main\java src\main\java\ns\Main.java
if %errorlevel% neq 0 ( echo ERREUR de compilation. & pause & exit /b 1 )

REM Copier les ressources images dans out/
if exist "src\main\resources" xcopy /E /Y /Q "src\main\resources\*" "out\" >nul

echo Lancement...
java -cp "out;lib\mysql-connector-j.jar" ns.Main
pause
