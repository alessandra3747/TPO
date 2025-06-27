@echo off

set "JAR_PATH=C:\Users\olafu\Desktop\TPO7_FA_S30395\target"

set "JAR_NAME=TPO7_FA_S30395-0.0.1-SNAPSHOT.jar"

cd /d "%JAR_PATH%"

echo Starting JAR...

java -jar "%JAR_NAME%"


pause
endlocal