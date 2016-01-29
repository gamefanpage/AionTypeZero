@ECHO off
color 1f
TITLE TypeZero AION 4.8 Game Server - Console

:START
echo.
SET PATH="C:\Program Files\Java\jdk1.7.0_80\bin"

IF "%MODE%" == "" (
CALL PanelCS.bat
)
ECHO TypeZero AION 4.8 Chat Server in %MODE% mode.
JAVA %JAVA_OPTS% -cp ./libs/*;TZ-chat.jar org.typezero.chatserver.ChatServer
SET CLASSPATH=%OLDCLASSPATH%
IF ERRORLEVEL 2 GOTO START
IF ERRORLEVEL 1 GOTO ERROR
IF ERRORLEVEL 0 GOTO END
:ERROR
ECHO.
ECHO Chat Server has terminated abnormaly!
ECHO.
PAUSE
EXIT
:END
ECHO.
ECHO Chat Server is terminated!
ECHO.
PAUSE
EXIT
