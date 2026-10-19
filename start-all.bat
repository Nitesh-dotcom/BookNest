@echo off
title BookNest - Master Launcher
echo ===================================================
echo             Starting BookNest Bookstore
echo ===================================================

echo 1. Starting MySQL 8.0 on port 3307 in new window...
start "BookNest MySQL" cmd /k "%~dp0start-mysql.bat"

echo Waiting 5 seconds for database initialization...
timeout /t 5 /nobreak > nul

echo 2. Starting Spring Boot Backend on port 8080 in new window...
start "BookNest Backend" cmd /k "%~dp0start-backend.bat"

echo Waiting 15 seconds for backend initialization...
timeout /t 15 /nobreak > nul

echo 3. Opening BookNest Frontend in default browser...
start "" "%~dp0frontend\index.html"

echo ===================================================
echo BookNest is running!
echo Storefront:  frontend\index.html
echo Backend API: http://localhost:8080/api
echo ===================================================
pause
