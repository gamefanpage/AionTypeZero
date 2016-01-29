:######################################################################## 
:# File name: BuildAll.bat
:# Edited Last By: Magenik 
:# V 1.0 1
:######################################################################## 

@ECHO off
@COLOR 0B
SET MODE=clean package
SET TITLE=Build
TITLE Aion Engine Aion Emulator - %TITLE% Panel
:MENU
CLS
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
ECHO                Aion Engine Aion Emulator - %TITLE% Panel 
ECHO                                  ''~``
ECHO                                 ( o o )
ECHO    ------------------------.oooO--(_)--Oooo.------------------------
ECHO    .             1 - Build GameServer server                       .
ECHO    .             2 - Build LoginServer server                      .
ECHO    .             3 - Build ChatServer server                       .
ECHO    .             4 - Build Commons                                 .
ECHO    .             5 - Build All server                              .
ECHO    .             6 - Build AE-Manager server                       .
ECHO    .             7 - Build AE_GeoData  Tools                       .
ECHO    .             8 - Quit                                          .
ECHO    .                         .oooO                                 .
ECHO    .                         (   )   Oooo.                         .
ECHO    ---------------------------\ (----(   )--------------------------
ECHO                                \_)    ) /
ECHO                                      (_/
ECHO.
:ENTER
SET /P Ares= Type your option and press ENTER:
IF %Ares%==1 GOTO GameServer
IF %Ares%==2 GOTO LoginServer
IF %Ares%==3 GOTO ChatServer
IF %Ares%==4 GOTO Commons
IF %Ares%==5 GOTO FULL
IF %Ares%==6 GOTO AE-Manager
IF %Ares%==7 GOTO AE_GeoData
IF %Ares%==8 GOTO QUIT
:FULL
cd ..\AE-Commons
start /WAIT /B ..\tools\Ant\bin\ant clean dist
cd ..\AE-Game
start /WAIT /B ..\tools\Ant\bin\ant clean dist
cd ..\AE-Login
start /WAIT /B ..\tools\Ant\bin\ant clean dist
cd ..\AE-Chat
start /WAIT /B ..\tools\Ant\bin\ant clean dist
cd AE-Manager
start /WAIT /B ..\tools\Ant\bin\ant clean dist
cd AE_GeoDataTools
start /WAIT /B ..\tools\Ant\bin\ant clean dist
GOTO :QUIT

:Commons
CALL Build_Maven_Commons.bat
GOTO :QUIT

:GameServer
cd ..\AE-Game
start /WAIT /B ..\tools\Ant\bin\ant clean dist
GOTO :QUIT

:LoginServer
cd ..\AE-Login
start /WAIT /B ..\tools\Ant\bin\ant clean dist
GOTO :QUIT

:ChatServer
cd ..\AE-Chat
start /WAIT /B ..\tools\Ant\bin\ant clean dist
GOTO :QUIT

:AE-Manager
cd AE-Manager
start /WAIT /B  ..\Ant\bin\ant clean dist
GOTO :QUIT

:AE_GeoData
cd AE_GeoDataTools
start /WAIT /B  ..\Ant\bin\ant clean dist
GOTO :QUIT

:QUIT
exit
