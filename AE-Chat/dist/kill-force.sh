#!/bin/sh

AION_PATH=/AionServer/ChatServer

if [ ! -d $AION_PATH/log ]; then
mkdir $AION_PATH/log
fi

echo "===============================================================" | tee -a /$AION_PATH/log/syslog
echo "=                  SHUTTING DOWN SERVERS                      =" | tee -a /$AION_PATH/log/syslog
echo "=                      PLEASE WAIT..                          =" | tee -a /$AION_PATH/log/syslog
echo "===============================================================" | tee -a /$AION_PATH/log/syslog
echo "" | tee -a /$AION_PATH/log/syslog
echo "" | tee -a /$AION_PATH/log/syslog

pkill -9 chatserver
echo "STOPPING CHATSERVER..!" | tee -a /$AION_PATH/log/syslog

pkill -9 start_cs.sh
echo "STOPPING CHATSERVER BASH SCRIPT..!" | tee -a /$AION_PATH/log/syslog

echo "" | tee -a /$AION_PATH/log/syslog
echo "" | tee -a /$AION_PATH/log/syslog
echo "===============================================================" | tee -a /$AION_PATH/log/syslog
echo "=                    ALL INSTANCES DOWN                       =" | tee -a /$AION_PATH/log/syslog
echo "=                      SERVER OFFLINE                         =" | tee -a /$AION_PATH/log/syslog
echo "===============================================================" | tee -a /$AION_PATH/log/syslog
