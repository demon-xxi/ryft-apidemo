#!/bin/bash

# Target install directory
target=${1-/opt}
# User running the site
user=ryft

# Versions
ryft_version=1.0-SNAPSHOT
jdk8_version=25
jdk_build_version=17
mvn_version=3.2.5

echo "### Checking Ryft User"
id ${user} > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "  --> OK"
else
    sudo useradd -m -s /bin/bash -U ${user}
    sudo usermod -a -G ${user} ${user}
fi

echo "### Checking Git"
git --version > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "  --> OK"
else
    echo "### Installing Git"
    sudo apt-get install -y git
    git --version
    if [ $? -ne 0 ]; then
        echo "Error installing Git"
        exit 1
    fi
fi

echo "### Checking Java"
java -version > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "  --> OK"
else
    echo "### Installing Java"
    wget --no-check-certificate -c --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u${jdk8_version}-b${jdk_build_version}/jdk-8u${jdk8_version}-linux-x64.tar.gz
    tar -xzf jdk-8u${jdk8_version}-linux-x64.tar.gz
    sudo mkdir -p /usr/lib/jvm
    sudo mv jdk1.8.0_${jdk8_version} /usr/lib/jvm
    sudo update-alternatives --install "/usr/bin/java" "java" "/usr/lib/jvm/jdk1.8.0_${jdk8_version}/bin/java" 1
    rm -f jdk-8u${jdk8_version}-linux-x64.tar.gz
    java -version
    if [ $? -ne 0 ]; then
        echo "Error installing Java"
        exit 2
    fi
fi

echo "### Checking Maven"
mvn -version > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "  --> OK"
else
    echo "### Installing Maven"
    wget http://apache.sunsite.ualberta.ca/maven/maven-3/${mvn_version}/binaries/apache-maven-${mvn_version}-bin.tar.gz
    sudo tar xzf apache-maven-${mvn_version}-bin.tar.gz -C /opt
    sudo update-alternatives --install "/usr/bin/mvn" "mvn" "/opt/apache-maven-${mvn_version}/bin/mvn" 1
    rm -f apache-maven-${mvn_version}-bin.tar.gz
    mvn -version
    if [ $? -ne 0 ]; then
        echo "Error installing Maven"
        exit 3
    fi
fi

if [ ! -d apidemo ]; then
    echo "### Cloning Ryft Demo Git repository"
    git clone https://github.com/getryft/apidemo.git
    if [ $? -ne 0 ]; then
        echo "Error cloning Git repository"
        exit 4
    fi
    cd apidemo
else 
    echo "### Pulling latest changes from Git repository"
    cd apidemo
    git pull
    if [ $? -ne 0 ]; then
        echo "Error pulling latest changes from Git repository"
        exit 4
    fi
fi
echo "  --> OK"
echo "### Compiling"
cd ryft-demo
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "Error building Ryft Demo project"
    exit 5
fi
echo "  --> OK"

echo "### Installing"
if [ -d ${target}/ryft-demo ]; then
    cp ${target}/ryft-demo/conf/ryft.properties /tmp
fi
sudo rm -rf ${target}/ryft-demo-${ryft_version}
sudo tar xzf target/ryft-demo-${ryft_version}-bin.tar.gz -C ${target}
sudo chown -R ryft:ryft ${target}/ryft-demo-${ryft_version}
sudo rm -f /opt/ryft-demo
sudo ln -s ${target}/ryft-demo-${ryft_version} /opt/ryft-demo
if [ -f /tmp/ryft.properties ]; then
    sudo cp /tmp/ryft.properties ${target}/ryft-demo/conf
    rm -f /tmp/ryft.properties
fi

cat >/tmp/ryft-demo.conf<< EOF
description "Ryft Demo"

start on runlevel [2345]
stop on runlevel [!2345]

respawn limit 2 5

limit nofile 8192 8192

setuid ryft
setgid ryft

exec java -cp '${target}/ryft-demo/conf:${target}/ryft-demo/lib/*' -Xmx512m com.metasys.ryft.DemoApplication ${target}/ryft-demo/webapp

EOF
sudo cp /tmp/ryft-demo.conf /etc/init/ryft-demo.conf
rm -f /tmp/ryft-demo.conf
sudo ln -fs /lib/init/upstart-job /etc/init.d/ryft-demo

echo "  --> OK"
echo "Edit '${target}/ryft-demo/conf/ryft.properties' to configure the demo site"
echo "Start the site running 'sudo service ryft-demo start'"

exit 0
