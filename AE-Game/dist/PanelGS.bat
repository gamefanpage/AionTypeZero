:########################################################################
:# File name: PanelGS.bat
:# Created By: Magenik
:# V 4.8
:########################################################################

@ECHO off
@COLOR 0B
TITLE Aion Type Zero Engine Emulator - Game Server Panel
:MENU
CLS
ECHO.
ECHO  ______                          ______
ECHO /\  _  \  __                    /\  ___\
ECHO \ \ \ \ \/\_\    ___     ___    \ \ \____    ___      __   __    ___      __
ECHO  \ \  __ \/\ \  / __`\ /' _ `\   \ \  ___\ /' _ `\  /'__`\/\_\ /' _ `\  /'__`\
ECHO   \ \ \/\ \ \ \/\ \ \ \/\ \/\ \   \ \ \____/\ \/\ \/\ \ \ \/\ \/\ \/\ \/\  __/
ECHO    \ \_\ \_\ \_\ \____/\ \_\ \_\   \ \_____\ \_\ \_\ \___, \ \ \ \_\ \_\ \____\
ECHO     \/_/\/_/\/_/\/___/  \/_/\/_/    \/_____/\/_/\/_/\/_____ \ \_\/_/\/_/\/____/
ECHO                                                        /\____\/_/
ECHO                                                        \/____/
ECHO.
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                Aion Engine Emulator - Game Server Panel                  ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                                                                          ^|
ECHO   ^|    1 - Development                                     4 - Live Server   ^|
ECHO   ^|    2 - Production                                      5 - Quit          ^|
ECHO   ^|    3 - Production X2                                                     ^|
ECHO   ^|                                                                          ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO.
SET /P OPTION=Type your option and press ENTER:
IF %OPTION% == 1 (
SET MODE=DEVELOPMENT
SET JAVA_OPTS=-Xms3072m -Xmx3072m -XX:MaxHeapSize=3072m -Xdebug -Xrunjdwp:transport=dt_socket,address=8998,server=y,suspend=n -ea
CALL StartGS.bat
)
IF %OPTION% == 2 (
SET MODE=PRODUCTION
SET JAVA_OPTS=-Xms1536m -Xmx1536m -server
CALL StartGS.bat
)
IF %OPTION% == 3 (
SET MODE=PRODUCTION X2
SET JAVA_OPTS=-Xms3872m -Xmx3872m -server
CALL StartGS.bat
)

IF %OPTION% == 4 (
SET MODE=LIVE
SET JAVA_OPTS=-Xms8192m -Xmx8192m -server
CALL StartGS.bat
)


IF %OPTION% == 5 (
EXIT
)
IF %OPTION% GEQ 6 (
GOTO :MENU
)
