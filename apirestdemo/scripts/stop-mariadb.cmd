@echo off
echo Deteniendo MariaDB...
"C:\Program Files\MariaDB 13.0\bin\mariadb-admin.exe" -h 127.0.0.1 -u root --password=root shutdown
echo Listo.
pause
