@echo off
title BookNest - Spring Boot Backend (Port 8080)
echo Setting Java 17 and Maven paths...
set "JAVA_HOME=C:\Users\nkp61\tools\jdk-17"
set "PATH=%JAVA_HOME%\bin;C:\Users\nkp61\tools\maven\bin;%PATH%"

echo Starting BookNest Spring Boot backend on http://localhost:8080/api ...
cd /d "%~dp0backend"
java -jar target\booknest-backend-1.0.0.jar
pause
