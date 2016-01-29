@ECHO off
color 1f
TITLE Type Zero AION 4.8 Game Server - Console

:START
echo.
SET PATH="C:\Program Files\Java\jdk1.7.0_80\bin"

JAVA -Dfile.encoding=UTF-8 -Xms512m -Xmx1356m -Xbootclasspath/p:libs/jsr166-1.0.0.jar -ea -javaagent:./libs/ae-commons-1.4.jar -cp ./libs/*;TZ-Game.jar org.typezero.gameserver.GameServer
REM -------------------------------------

SET CLASSPATH=%OLDCLASSPATH%

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
if ERRORLEVEL 0 goto end

REM Restart...
:restart
echo.
echo Administrator Restart ...
echo.
goto start

REM Error...
:error
echo.
echo Server terminated abnormaly ...
echo.
goto end

REM End...
:end
echo.
echo Server terminated ...
echo.
pause
