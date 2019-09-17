#!/bin/sh

ClientDesktop='../../../build/libs/com.fiskaly.kassensichv.client.desktop-0.0.1-alpha.jar'

#run GeneralSMATest
javac -cp ${ClientDesktop}:. GeneralSMATest.java

java -cp ${ClientDesktop}:. GeneralSMATest

#cleanup
rm GeneralSMATest.class

