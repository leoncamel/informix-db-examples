
# Reproduce the issue Informix CDC with LVARCHAR datatype

## Step 1, run informix database docker

Since the official docker images from `ibmcom/informix-developer-database:12.10.FC12W1DE` does not enable
CDC functionalities, we build our own docker image for Informix CDC Developers [xiaolin/ifx12-cdc-test:v1](https://hub.docker.com/repository/docker/xiaolin/ifx12-cdc-test)

Now, we pull and run this docker image:

```text
$ docker run -it --name ifx --privileged -e SIZE=small \
    -p 9088:9088      \
    -p 9089:9089      \
    -p 27017:27017    \
    -p 27018:27018    \
    -p 27883:27883    \
    -e LICENSE=accept \
    xiaolin/ifx12-cdc-test:v1
```

## Step 2, create LVARCHAR table for testing

Notes, we create this table in `testdb`:

```text
create table if not exists test_lvarchar(a lvarchar(400));
```

## Step 3, now we run our demo CDC program

```text
$ ./gradlew clean shadowJar && ./run.sh cdc
```

## Step 4, insert one row into the table

```text
select * from testdb:informix.test_lvarchar;

insert into testdb:informix.test_lvarchar values("hello");
```

Then, switch back to you cdc terminal, you will see the prompt like this:

```text
./gradlew clean shadowJar && ./run.sh cdc

Deprecated Gradle features were used in this build, making it incompatible with Gradle 8.0.

You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.

See https://docs.gradle.org/7.4.2/userguide/command_line_interface.html#sec:command_line_warnings

BUILD SUCCESSFUL in 2s
4 actionable tasks: 4 executed
+ JAVA_OPTS=-Dlog4j2.configurationFile=src/main/resources/log4j2.xml
+ /Users/xiaolin/Library/Java/JavaVirtualMachines/openjdk-17.0.2/Contents/Home/bin/java -Dlog4j2.configurationFile=src/main/resources/log4j2.xml -jar build/libs/cdc-all.jar cdc
WARNING: sun.reflect.Reflection.getCallerClass is not supported. This will impact performance.
11:27:33.191 [main] INFO  cdc.IfxJdbcMain - [METADATA] Label [1] Columns: [Column Type: java.lang.String
SQL Type: 43
Extended ID: 0]
11:27:42.633 [main] INFO  cdc.IfxJdbcMain - [TIMEOUT] Sequence ID [0]
11:27:52.336 [main] INFO  cdc.IfxJdbcMain - [BEGIN] Transaction ID [32] Sequence ID [77309870104]
11:27:52.369 [main] INFO  cdc.IfxJdbcMain - [INSERT] Transaction ID [32] Sequence ID [77309870160] Label [1]
com.informix.stream.impl.IfxStreamException: Error processing CDC data stream
	at com.informix.stream.cdc.records.IfxCDCOperationRecord.getData(IfxCDCOperationRecord.java:121)
	at cdc.CdcMain.call(CdcMain.java:112)
	at cdc.CdcMain.call(CdcMain.java:37)
	at picocli.CommandLine.executeUserObject(CommandLine.java:1953)
	at picocli.CommandLine.access$1300(CommandLine.java:145)
	at picocli.CommandLine$RunLast.executeUserObjectOfLastSubcommandWithSameParent(CommandLine.java:2358)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:2352)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:2314)
	at picocli.CommandLine$AbstractParseResultHandler.execute(CommandLine.java:2179)
	at picocli.CommandLine$RunLast.execute(CommandLine.java:2316)
	at picocli.CommandLine.execute(CommandLine.java:2078)
	at cdc.Main.main(Main.java:26)
Caused by: java.lang.ArrayIndexOutOfBoundsException: Index 12 out of bounds for length 8
	at com.informix.jdbc.IfxLvarchar.fromIfx(IfxLvarchar.java:165)
	at com.informix.jdbc.IfxLvarchar.fromIfx(IfxLvarchar.java:191)
	at com.informix.jdbc.IfxLvarchar.fromIfx(IfxLvarchar.java:184)
	at com.informix.stream.cdc.records.IfxCDCOperationRecord.getData(IfxCDCOperationRecord.java:77)
	... 11 more
11:28:07.433 [main] INFO  cdc.IfxJdbcMain - Command Exit with code = 0
```

