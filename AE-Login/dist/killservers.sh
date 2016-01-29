#!/bin/sh

AION_PATH=/AionServer/LoginServer

if [ ! -d $AION_PATH/log ]; then
mkdir $AION_PATH/log
fi

test -z "$(pidof -x start_ls.sh)" && kill $(pidof -x start_ls.sh)

/usr/bin/killall loginserver
/bin/kill `ps -ef | grep ae_loginserver.jar | grep -v grep | awk '{print $2}'`
echo "Kill Login Server complete" | tee -a /$AION_PATH/log/syslog
