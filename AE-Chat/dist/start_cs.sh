#!/bin/sh

echo ""
echo -e '\E[37;44m'"\033[1m*-------------------------------------------------------------*\033[0m"
echo -e '\E[37;44m'"\033[1m|           Type Zero Engine - ChatServer Panel               |\033[0m"
echo -e '\E[37;44m'"\033[1m*-------------------------------------------------------------*\033[0m"
echo -e '\E[37;44m'"\033[1m|                    Starting the Engine                      |\033[0m"
echo -e '\E[37;44m'"\033[1m|                Loading up Aion Chat Server                  |\033[0m"
echo -e '\E[37;44m'"\033[1m*-------------------------------------------------------------*\033[0m"
echo ""

err=1
until [ ${err} == 0 ];
do
	java -Xms128m -Xmx128m -ea -Xbootclasspath/p:./libs/jsr166-1.0.0.jar -javaagent:libs/ae-commons-1.4.jar -cp ./libs/*:TZ-chat.jar org.typezero.chatserver.ChatServer
	err=$?
	cspid=$!
	echo ${cspid} > chatserver.pid
	echo -e '\E[37;44m'"\033[1m*-------------------------------------------------------------*\033[0m"
	echo -e '\E[37;44m'"\033[1m|                      Startup finished                       |\033[0m"
	echo -e '\E[37;44m'"\033[1m*-------------------------------------------------------------*\033[0m"
	echo -e '\E[37;44m'"\033[1m|                Aion Chat Server is online                   |\033[0m"
	echo -e '\E[37;44m'"\033[1m*-------------------------------------------------------------*\033[0m"
	sleep 10
done
