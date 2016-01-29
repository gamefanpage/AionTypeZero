#!/bin/sh

echo ""
echo -e '\E[37;44m'"\033[1m*-------------------------------------------------------------*\033[0m"
echo -e '\E[37;44m'"\033[1m|          Type Zero Engine - GameServer Panel                |\033[0m"
echo -e '\E[37;44m'"\033[1m*-------------------------------------------------------------*\033[0m"
echo -e '\E[37;44m'"\033[1m|                    Starting the Engine                      |\033[0m"
echo -e '\E[37;44m'"\033[1m|                Loading up Aion Game Server                  |\033[0m"
echo -e '\E[37;44m'"\033[1m*-------------------------------------------------------------*\033[0m"
echo ""

err=1
until [ ${err} == 0 ];
do

	java -Xms256m -Xmx8192m -ea -XX:-UseSplitVerifier -javaagent:./libs/ae-commons-1.4.jar -cp ./libs/*:TZ-Game.jar org.typezero.gameserver.GameServer
	err=$?
	gspid=$!
	echo ${gspid} > gameserver.pid
	echo -e '\E[37;44m'"\033[1m*-------------------------------------------------------------*\033[0m"
	echo -e '\E[37;44m'"\033[1m|                      Startup finished                       |\033[0m"
	echo -e '\E[37;44m'"\033[1m*-------------------------------------------------------------*\033[0m"
	echo -e '\E[37;44m'"\033[1m|                Aion Game Server is online                   |\033[0m"
	echo -e '\E[37;44m'"\033[1m*-------------------------------------------------------------*\033[0m"
	sleep 10
done
