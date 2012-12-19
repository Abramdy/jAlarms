To run the client and/or server, you need to place some symlinks here, to Spring 3.1, slf4j 1.7, and the protobuf jar you are using, something like this:

ln -s $HOME/projects/spring-3.1.3/dist spring
ln -s $HOME/projects/google/protobuf/java/target/protobuf-java-2.4.1.jar protobuf.jar
ln -s $HOME/projects/slf4j-1.7.1 slf4j-1.7.1
