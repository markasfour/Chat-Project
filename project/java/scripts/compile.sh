#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
# Indicate the path of the java compiler to use
# export JAVA_HOME=/usr/csshare/pkgs/jdk1.7.0_17
# export PATH=$JAVA_HOME/bin:$PATH

# compile the java program

CLASSPATH="${DIR::-12}classes"
LIBPATH="${DIR::-7}/lib/postgresql-9.4.1208.jre6.jar"
HOSTNAME=chat-messenger.cjkipm6h5qsr.us-west-1.rds.amazonaws.com
DB_NAME=chat
PGPORT=5432
USER=chat
PASSWD=finish


echo $CLASSPATH
echo $HOSTNAME


javac -d $CLASSPATH $DIR/../src/Messenger.java

#run the java program
#Use your database name, port number and login
java -cp $CLASSPATH:$LIBPATH Messenger $HOSTNAME $DB_NAME $PGPORT $USER $PASSWD

