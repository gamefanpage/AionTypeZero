#!/bin/sh

echo ""
echo -e '\E[37;44m'"\033[1m*-------------------------------------------------------------*\033[0m"
echo -e '\E[37;44m'"\033[1m|          Type Zero Engine - LoginServer Panel               |\033[0m"
echo -e '\E[37;44m'"\033[1m*-------------------------------------------------------------*\033[0m"
echo -e '\E[37;44m'"\033[1m|                    Starting the Engine                      |\033[0m"
echo -e '\E[37;44m'"\033[1m|                Loading up Aion Login Server                 |\033[0m"
echo -e '\E[37;44m'"\033[1m*-------------------------------------------------------------*\033[0m"
echo ""

err=1
until [ ${err} == 0 ];
do
	java -Xms8m -Xmx32m -ea -Xbootclasspath/p:./libs/jsr166-1.0.0.jar -javaagent:libs/ae-commons-1.4.jar -cp ./libs/*:ae_loginserver.jar com.aionengine.loginserver.LoginServer
	err=$?
	lspid=$!
	echo ${lspid} > loginserver.pid
	echo -e '\E[37;44m'"\033[1m*=== Aion Login Server is Started! ===*\033[0m"
	sleep 10
done
echo ""
echo -e '\E[37;44m'"\033[1m*-------------------------------------------------------------*\033[0m"
echo -e '\E[37;44m'"\033[1m|                      Startup finished                       |\033[0m"
echo -e '\E[37;44m'"\033[1m*-------------------------------------------------------------*\033[0m"
echo -e '\E[37;44m'"\033[1m|                Aion Login Server is online                  |\033[0m"
echo -e '\E[37;44m'"\033[1m*-------------------------------------------------------------*\033[0m"
