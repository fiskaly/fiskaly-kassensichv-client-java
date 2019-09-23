@echo off

SET ClientGeneral=../../../build/libs/com.fiskaly.kassensichv.client.general-0.0.1-alpha.jar

REM run GeneralSMATest
javac -cp %ClientGeneral%;. GeneralSMATest.java

java -cp %ClientGeneral%;. GeneralSMATest

REM cleanup
del GeneralSMATest.class
