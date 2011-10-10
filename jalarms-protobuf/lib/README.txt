You need to place some symlinks here, to Spring 3.0 or 3.1, slf4j 1.6.1, and the protobuf jar you are using, something like this:

ln -s $HOME/projects/spring-3.0.6/dist spring
ln -s $HOME/projects/google/protobuf/java/target/protobuf-java-2.4.1.jar protobuf.jar
ln -s $HOME/projects/slf4j-1.6.1 slf4j-1.6.1
