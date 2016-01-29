@ECHO off
SET MODE=clean package
SET TITLE=Build
TITLE Aion Engine - %TITLE% Panel
:MENU
CLS
ECHO.
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                    Aion Engine Commons - %TITLE% Panel                     ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                                                                          ^|
ECHO   ^|    1 - Build Commons                                2 - Quit             ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO.
:ENTER
SET /P OPTION=Type your option and press ENTER: 
IF %OPTION% == 1 (
CLS
TITLE Aion Engine Commons - %TITLE%ing Commons
CD ../AE-Commons
call mvn %MODE%
pause
)
IF %OPTION% == 9 (
EXIT
)
IF %OPTION% GEQ 10 (
GOTO :MENU
)
