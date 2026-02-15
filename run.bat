@echo off
REM Run script for Table Tennis Pro

echo ========================================
echo Starting Table Tennis Pro
echo ========================================
echo.

REM Check if bin directory exists
if not exist "bin" (
    echo Error: Application not compiled yet!
    echo Please run build.bat first.
    echo.
    pause
    exit /b 1
)

REM Run the application
java -cp bin com.tabletennispro.MainWindow

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ========================================
    echo Error running application!
    echo ========================================
    echo.
    pause
)
