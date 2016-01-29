:########################################################################
:# File name: PanelCS.bat
:# Created By: Magenik
:# V 4.8
:########################################################################

@ECHO off
@COLOR 0B
TITLE Aion Type Zero Engine Emulator - Chat Server Panel
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
ECHO   ^|                Aion Engine Emulator - Chat Server Panel                  ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                                                                          ^|
ECHO   ^|    1 - Development                                     3 - Quit          ^|
ECHO   ^|    2 - Production                                                        ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO.
SET /P OPTION=Type your option and press ENTER:
IF %OPTION% == 1 (
SET MODE=DEVELOPMENT
SET JAVA_OPTS=-Xms128m -Xmx128m -Xdebug -Xrunjdwp:transport=dt_socket,address=8997,server=y,suspend=n -ea
CALL StartCS.bat
)
IF %OPTION% == 2 (
SET MODE=PRODUCTION
SET JAVA_OPTS=-Xms64m -Xmx64m -server
CALL StartCS.bat
)
IF %OPTION% == 3 (
EXIT
)
IF %OPTION% GEQ 4 (
GOTO :MENU
)
