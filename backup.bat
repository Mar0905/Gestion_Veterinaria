@echo off
setlocal

set DB_NAME=gestion_veterinaria
set DB_USER=root
set DB_PASS=
set BACKUP_DIR=backups

for /f "tokens=1-3 delims=/" %%a in ("%date%") do (
    set DAY=%%a
    set MONTH=%%b
    set YEAR=%%c
)
set DATE=%YEAR%%MONTH%%DAY%
set FILE=%BACKUP_DIR%\vetsys_%DATE%.sql

if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

if "%DB_PASS%"=="" (
    mysqldump -u %DB_USER% %DB_NAME% > "%FILE%"
) else (
    mysqldump -u %DB_USER% -p%DB_PASS% %DB_NAME% > "%FILE%"
)

if %ERRORLEVEL%==0 (
    echo Backup creado: %FILE%
) else (
    echo Error al crear el backup
    exit /b 1
)

endlocal
