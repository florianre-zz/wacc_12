.PHONY: all clean test

all:
	mvn -q compile

clean:
	mvn -q clean

test:
	mvn test
