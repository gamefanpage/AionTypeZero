#!/bin/sh

AION_PATH=/AionServer/ChatServer

if [ ! -d $AION_PATH/log ]; then
mkdir $AION_PATH/log
fi

test -z "$(pidof -x start_cs.sh)" && kill $(pidof -x start_cs.sh)

/usr/bin/killall chatserver
/bin/kill `ps -ef | grep TZ-chat.jar | grep -v grep | awk '{print $2}'`
echo "Kill Chat Server complete" | tee -a /$AION_PATH/log/syslog
