@echo off
echo ===============================================
echo  Iniciando MariaDB local (puerto 3306)
echo  Cierra esta ventana para DETENER el servidor
echo ===============================================
"C:\Program Files\MariaDB 13.0\bin\mysqld.exe" --datadir="C:\Users\balmo\mariadb-data" --port=3306 --console
