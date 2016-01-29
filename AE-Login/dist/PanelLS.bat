:######################################################################## 
:# File name: PanelLS.bat
:# Edited Last By: Magenik 
:# V 1.0 1
:########################################################################

@ECHO off
@COLOR 0B
TITLE Aion Engine Emulator - Login Server Panel
:MENU
CLS
ECHO.
echo  ______                          ______                                         
echo /\  _  \  __                    /\  ___\                                        
echo \ \ \ \ \/\_\    ___     ___    \ \ \____    ___      __   __    ___      __    
echo  \ \  __ \/\ \  / __`\ /' _ `\   \ \  ___\ /' _ `\  /'__`\/\_\ /' _ `\  /'__`\  
echo   \ \ \/\ \ \ \/\ \ \ \/\ \/\ \   \ \ \____/\ \/\ \/\ \ \ \/\ \/\ \/\ \/\  __/  
echo    \ \_\ \_\ \_\ \____/\ \_\ \_\   \ \_____\ \_\ \_\ \___, \ \ \ \_\ \_\ \____\ 
echo     \/_/\/_/\/_/\/___/  \/_/\/_/    \/_____/\/_/\/_/\/_____ \ \_\/_/\/_/\/____/ 
echo                                                        /\____\/_/               
echo                                                        \/____/                  
echo.
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                Aion Engine Emulator - Login Server Panel                 ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                                                                          ^|
ECHO   ^|    1 - Development                                       3 - Quit        ^|
ECHO   ^|    2 - Production                                                        ^|
ECHO   ^|                                                                          ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO.
SET /P OPTION=Type your option and press ENTER: 
IF %OPTION% == 1 (
SET MODE=DEVELOPMENT
SET JAVA_OPTS=-Xms32m -Xmx32m -Xdebug -Xrunjdwp:transport=dt_socket,address=8999,server=y,suspend=n -ea
CALL StartLS.bat
)
IF %OPTION% == 2 (
SET MODE=PRODUCTION
SET JAVA_OPTS=-Xms64m -Xmx64m -server
CALL StartLS.bat
)
IF %OPTION% == 3 (
EXIT
)
IF %OPTION% GEQ 4 (
GOTO :MENU
)