#!/bin/bash

javac $1.java
java -classpath ../ojdbc6.jar:. $1
