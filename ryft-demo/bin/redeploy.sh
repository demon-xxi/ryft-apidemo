#!/bin/bash

version=1.0-SNAPSHOT

git pull
cd ryft-demo
cp target/ryft-demo-${version}/conf/ryft.properties ryft.properties.backup
mvn clean package -DskipTests
cd target
tar xzf ryft-demo-${version}-bin.tar.gz
cd ryft-demo-${version}
cp ../../ryft.properties.backup  conf/ryft.properties
pid=`ps -o pid,command | grep -e java -e DemoApplication | grep -v grep | awk -F' ' '{print $1}'`
kill $pid
nohup java -cp 'conf:lib/*' com.metasys.ryft.DemoApplication &
tail -f nohup.out
