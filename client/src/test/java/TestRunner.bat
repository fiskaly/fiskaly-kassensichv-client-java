@echo off

SET ClientDesktop=../../../build/libs/com.fiskaly.kassensichv.client.desktop-0.0.1-alpha.jar

REM run GeneralSMATest
javac -cp %ClientDesktop%;. GeneralSMATest.java

java -cp %ClientDesktop%;. GeneralSMATest

REM cleanup
del GeneralSMATest.class