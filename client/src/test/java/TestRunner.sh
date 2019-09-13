#!/bin/sh

#run GeneralSMATest
javac -cp "../../../build/libs/clientDesktop-0.0.1-alpha.jar" GeneralSMATest.java

java -cp "../../../build/libs/clientDesktop-0.0.1-alpha.jar" GeneralSMATest.java

#cleanup
rm GeneralSMATest.class

