#!/bin/sh

ClientGeneral='../../../build/libs/com.fiskaly.kassensichv.client.general-0.0.1-alpha.jar'

#run GeneralSMATest
javac -cp ${ClientGeneral}:. GeneralSMATest.java

java -cp ${ClientGeneral}:. GeneralSMATest

#cleanup
rm GeneralSMATest.class

