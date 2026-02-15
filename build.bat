@echo off
REM Build script for Table Tennis Pro

echo ========================================
echo Building Table Tennis Pro
echo ========================================

REM Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

REM Compile all Java files
echo Compiling Java files...
javac -d bin src\main\java\com\tabletennispro\*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Build successful!
    echo ========================================
    echo.
    echo To run the application, use:
    echo   run.bat
    echo.
    echo Or manually with:
    echo   java -cp bin com.tabletennispro.MainWindow
    echo.
) else (
    echo.
    echo ========================================
    echo Build failed! Please check for errors.
    echo ========================================
    echo.
)

pause
