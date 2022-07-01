#!/bin/bash


# export JAVA_HOME=$HOME/local/jdk8u282-b08/Contents/Home/
export JAVA_HOME=$HOME/Library/Java/JavaVirtualMachines/openjdk-17.0.2/Contents/Home

set -x

# ARGS="jdbc:informix-sqli://172.20.3.242:9088/syscdcv1:user=informix;password=in4mix"
# ARGS="jdbc:informix-sqli://172.20.3.242:9088/syscdcv1:user=informix;password=in4mix;PROTOCOLTRACE=2;PROTOCOLTRACEFILE=/tmp/proto_trace.out;TRACE=3;TRACEFILE=/tmp/trace.out"
JAVA_OPTS="-Dlog4j2.configurationFile=src/main/resources/log4j2.xml"

$JAVA_HOME/bin/java $JAVA_OPTS -jar build/libs/cdc-all.jar $@

