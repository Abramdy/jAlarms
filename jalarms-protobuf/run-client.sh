SPRING=`ls -1 lib/spring/*.jar|xargs|sed "s/ /:/g"`
PROTOBUF=lib/protobuf.jar
SLF4J=lib/slf4j-1.6.1/slf4j-api-1.6.1.jar:lib/slf4j-1.6.1/slf4j-simple-1.6.1.jar:lib/slf4j-1.6.1/jcl-over-slf4j-1.6.1.jar
JALARMS=../jalarms-core/build/classes/main
groovy -cp $SPRING:$PROTOBUF:$SLF4J:$JALARMS:build/classes/main src/test/groovy/TestClient.groovy
