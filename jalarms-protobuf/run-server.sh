SPRING=`ls -1 lib/spring/*.jar|xargs|sed "s/ /:/g"`
PROTOBUF=lib/protobuf.jar
SLF4J=lib/slf4j-1.7.5/slf4j-api-1.7.5.jar:lib/slf4j-1.7.5/slf4j-simple-1.7.5.jar:lib/slf4j-1.7.5/jcl-over-slf4j-1.7.5.jar
JALARMS=../jalarms-core/build/classes/main
java -cp $SPRING:$PROTOBUF:$SLF4J:$JALARMS:build/classes/main:src/main/resources com.solab.alarms.spring.ProtoBootstrap
