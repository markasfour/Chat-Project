#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
# Indicate the path of the java compiler to use
export JAVA_HOME=/usr/csshare/pkgs/jdk1.7.0_17
export PATH=$JAVA_HOME/bin:$PATH

# compile the java program

#CLASSPATH="${DIR::-12}classes"
#DB_NAME=chat
#USER=chat

# FOR AMAZON RDS
# LIBPATH="${DIR::-7}/lib/postgresql-9.4.1208.jre6.jar"
# HOSTNAME=chat-messenger.cjkipm6h5qsr.us-west-1.rds.amazonaws.com
# PGPORT=5432
# PASSWD=finish

# echo $CLASSPATH
# echo $HOSTNAME

#javac -d $CLASSPATH $DIR/../src/Messenger.java
#run the java program
#Use your database name, port number and login
#java -cp $CLASSPATH:$LIBPATH Messenger $HOSTNAME $DB_NAME $PGPORT $USER $PASSWD

# FOR WELL
HOSTNAME=localhost
# compile the java program
javac -d $DIR/../classes $DIR/../src/Messenger.java

#run the java program
#Use your database name, port number and login
java -cp $DIR/../classes:$DIR/../lib/pg73jdbc3.jar Messenger $HOSTNAME $DB_NAME $PGPORT $USER



# FOR WELL
#LIBPATH="${DIR::-7}/lib/pg73jdbc3.jar"


