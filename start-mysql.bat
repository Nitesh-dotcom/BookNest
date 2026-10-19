@echo off
title BookNest - MySQL 8.0 (Port 3307)
echo Starting MySQL 8.0 on port 3307...
echo Datadir: C:\Users\nkp61\tools\mysql_data
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe" --datadir="C:\Users\nkp61\tools\mysql_data" --port=3307 --console
pause
