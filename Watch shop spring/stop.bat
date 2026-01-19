@echo off
echo Stopping Watch Store Application...
taskkill /F /IM java.exe

echo.
echo Stopping Database...
docker-compose stop

echo.
echo Done.
pause
