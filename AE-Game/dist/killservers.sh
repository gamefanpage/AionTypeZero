#!/bin/sh

AION_PATH=/AionServer/GameServer

if [ ! -d $AION_PATH/log ]; then
mkdir $AION_PATH/log
fi

test -z "$(pidof -x start_gs.sh)" && kill $(pidof -x start_gs.sh)

/usr/bin/killall gameserver
/bin/kill `ps -ef | grep ae_gameserver.jar | grep -v grep | awk '{print $2}'`
echo "Kill Game Server complete" | tee -a /$AION_PATH/log/syslog
