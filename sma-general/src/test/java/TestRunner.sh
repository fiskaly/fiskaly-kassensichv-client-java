#!/bin/sh

#copy jar file
cp ../../../build/libs/sma-general-0.0.1-alpha.jar sma-general-0.0.1-alpha.jar
cp ../../../../sma-common/build/libs/sma-common-0.0.1-alpha.jar sma-common-0.0.1-alpha.jar

#run GeneralSMATest
javac -cp "sma-general-0.0.1-alpha.jar:sma-common-0.0.1-alpha.jar" GeneralSMATest.java

java -cp "sma-general-0.0.1-alpha.jar:sma-common-0.0.1-alpha.jar" GeneralSMATest.java

#cleanup
rm GeneralSMATest.class
rm sma-general-0.0.1-alpha.jar
rm sma-common-0.0.1-alpha.jar
