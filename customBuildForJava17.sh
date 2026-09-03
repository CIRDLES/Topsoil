#!/bin/bash

echo "Hello Travis ****************************************"
echo ""
echo "getting Liberica 17 full   **************************"
wget -O  libjava17.tar.gz https://download.bell-sw.com/java/17.0.1+12/bellsoft-jdk17.0.1+12-linux-amd64-full.tar.gz
tar -xf libjava17.tar.gz
ls jdk-17.0.1-full
echo ""

echo "getting Gradle7            **************************"
wget -O  gradle7.zip https://services.gradle.org/distributions/gradle-7.4.2-bin.zip
mkdir gradle7
unzip -qd gradle7 gradle7.zip
ls gradle7/gradle-7.4.2
echo ""

echo "building Topsoil          **************************"
gradle7/gradle-7.4.2/bin/gradle clean build -Dorg.gradle.java.home=./jdk-17.0.1-full/