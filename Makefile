.PHONY: all clean test

all:
	echo export JAVA_HOME=/usr/lib/jvm/jdk-8-oracle-x64/ >> ~/.mavenrc
	mvn -q package

clean:
	mvn -q clean

test: clean
	mvn test
