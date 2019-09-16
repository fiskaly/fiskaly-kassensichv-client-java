#!/bin/sh

ClientDesktop='../../../build/libs/com.fiskaly.kassensichv.client.desktop-0.0.1-alpha.jar'

#get the current date to name the test output file
DATE=`date +%d-%m-%y`

#run GeneralSMATest
javac -cp ${ClientDesktop} GeneralSMATest.java

java -cp ${ClientDesktop} GeneralSMATest.java &> testoutput_${DATE}.txt

#cleanup
rm GeneralSMATest.class

