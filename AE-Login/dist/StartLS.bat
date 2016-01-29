@ECHO off
TITLE Aion Engine Emulator - Login Server Console
:START
CLS
IF "%MODE%" == "" (
CALL PanelLS.bat
)
ECHO Starting Aion Engine Login Server in %MODE% mode.
JAVA %JAVA_OPTS% -cp ./libs/*;ae_loginserver.jar com.aionengine.loginserver.LoginServer
SET CLASSPATH=%OLDCLASSPATH%
IF ERRORLEVEL 2 GOTO START
IF ERRORLEVEL 1 GOTO ERROR
IF ERRORLEVEL 0 GOTO END
:ERROR
ECHO.
ECHO Login Server has terminated abnormaly!
ECHO.
PAUSE
EXIT
:END
ECHO.
ECHO Login Server is terminated!
ECHO.
PAUSE
EXIT