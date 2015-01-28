#!/bin/bash

wget --no-check-certificate -c --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u25-b17/jdk-8u25-linux-x64.tar.gz
tar -xzf jdk-8u25-linux-x64.tar.gz
sudo mkdir -p /usr/lib/jvm
sudo mv jdk1.8.0_25 /usr/lib/jvm
sudo update-alternatives --install "/usr/bin/java" "java" "/usr/lib/jvm/jdk1.8.0_25/bin/java" 2000
sudo cat >>/etc/bash.bashrc<<EOF

export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_25
export PATH=\${PATH}:/usr/lib/jvm/jdk1.8.0_25/bin
EOF
rm -f jdk-8u25-linux-x64.tar.gz

javac com/metasys/ryft/Wrapper.java;java -Djava.library.path=.:/usr/lib/x86_64-linux-gnu com.metasys.ryft.Wrapper