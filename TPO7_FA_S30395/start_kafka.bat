@echo off


REM --- CONFIGURATION ---
set "KAFKA_HOME=C:\kafka_2.13-3.9.1"
set "JAR_PATH=C:\Users\olafu\Desktop\TPO7_FA_S30395\target"
set "JAR_NAME=TPO7_FA_S30395-0.0.1-SNAPSHOT.jar"

REM --- CHECK IF ZOOKEEPER IS RUNNING ---
echo Checking if ZooKeeper is running...
powershell -Command "try { (New-Object Net.Sockets.TcpClient('localhost',2181)).Close(); exit 0 } catch { exit 1 }"
set "ZK_RUNNING=%ERRORLEVEL%"

REM --- CHECK IF KAFKA IS RUNNING ---
echo Checking if Kafka is running...
powershell -Command "try { (New-Object Net.Sockets.TcpClient('localhost',9092)).Close(); exit 0 } catch { exit 1 }"
set "KAFKA_RUNNING=%ERRORLEVEL%"

REM --- START ZOOKEEPER IF NOT RUNNING ---
if "%ZK_RUNNING%" NEQ "0" (
    echo ZooKeeper not running. Starting ZooKeeper...
    cd /d "%KAFKA_HOME%"
    start "" .\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
    timeout /t 8 >nul
) else (
    echo ZooKeeper is already running.
)

REM --- START KAFKA IF NOT RUNNING ---
if "%KAFKA_RUNNING%" NEQ "0" (
    echo Kafka not running. Starting Kafka...
    cd /d "%KAFKA_HOME%"
    start "" .\bin\windows\kafka-server-start.bat .\config\server.properties
    timeout /t 8 >nul
) else (
    echo Kafka is already running.
)

echo End...
pause
endlocal