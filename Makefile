.PHONY: build run

build: 
	mvn compile assembly:single

run: 
	java -jar target/ld-ads-demo-1.0-SNAPSHOT-jar-with-dependencies.jar