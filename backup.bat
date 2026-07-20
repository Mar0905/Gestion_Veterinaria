@echo off
setlocal

set DB_NAME=gestion_veterinaria
set DB_USER=root
set DB_PASS=
set BACKUP_DIR=backups

set MYSQLDUMP=C:\xampp\mysql\bin\mysqldump.exe

for /f %%i in ('powershell -command "Get-Date -Format yyyyMMdd"') do set DATE=%%i

set FILE=%BACKUP_DIR%\vetsys_%DATE%.sql

if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

"%MYSQLDUMP%" -u %DB_USER% %DB_NAME% > "%FILE%"

if %ERRORLEVEL%==0 (
    echo.
    echo Backup creado correctamente:
    echo %FILE%
) else (
    echo.
    echo Error al crear el backup
)

pause
endlocal