#!/bin/bash

aiusername=$(whoami)

# mark the start time
STARTTIMEAPI=$(date +%s)

echo ""

echo '---'
echo 'Installing Ryft ONE API web demo'
echo '---'

echo ""

# Target install directory
target=/opt
shift

# Versions
ryft_version=1.0-SNAPSHOT
mvn_version=3.2.5

echo '---'
echo 'Checking git'
echo '---'

echo ""

git --version > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo '---'
    echo 'Installing git'
    echo '---'

    echo ""

    sudo apt-get install -y git
    git --version
    if [ $? -ne 0 ]; then
        echo "Error installing Git"
        exit 1
    fi
fi
echo "  --> OK"

echo ""

echo '---'
echo 'Checking maven'
echo '---'

echo ""

mvn -version > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo '---'
    echo 'Installing maven'
    echo '---'

    echo ""

    sudo apt-get install maven
    mvn -version
    if [ $? -ne 0 ]; then
        echo "*** Error installing Maven"
        exit 3
    fi
fi
echo "  --> OK"

echo ""

if [ ! -d apidemo ]; then
    echo '---'
    echo 'Cloning Ryft ONE API web demo Git repository'
    echo '---'

    echo ""

    git clone https://github.com/getryft/apidemo.git
    if [ $? -ne 0 ]; then
        echo "*** Error cloning Git repository"
        exit 4
    fi
    cd apidemo
else 
    echo '---'
    echo 'Pulling latest changes from Ryft ONE API web demo Git repository'
    echo '---'

    echo ""

    cd apidemo
    git pull
    if [ $? -ne 0 ]; then
        echo "*** Error pulling latest changes from Git repository"
        exit 4
    fi
fi
echo "  --> OK"

echo ""

echo '---'
echo 'Compiling Ryft ONE API web demo'
echo '---'

echo ""

cd ryft-demo
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "*** Error building Ryft Demo project"
    exit 5
fi
echo "  --> OK"

echo '---'
echo "Installing Ryft ONE API web demo for user ${aiusername}"
echo '---'

echo ""

if [ -d ${target}/ryft-demo ]; then
    cp ${target}/ryft-demo/conf/ryft.properties /tmp
fi
sudo rm -rf ${target}/ryft-demo-${ryft_version}
sudo tar xzf target/ryft-demo-${ryft_version}-bin.tar.gz -C ${target}
sudo chown -R ${aiusername}:ryftone ${target}/ryft-demo-${ryft_version}
sudo rm -f /opt/ryft-demo
sudo ln -s ${target}/ryft-demo-${ryft_version} /opt/ryft-demo
if [ -f /tmp/ryft.properties ]; then
    sudo cp /tmp/ryft.properties ${target}/ryft-demo/conf
    rm -f /tmp/ryft.properties
fi

cd ~/ryft/apidemo/ryft-demo/target 
tar xvf ryft-demo-1.0-SNAPSHOT-bin.tar.gz

sudo cp ~/ryft/apidemo/ryft-demo/bin/ryftwebapidemo-initd /etc/init.d/ryftwebapidemo
sudo chmod 755 /etc/init.d/ryftwebapidemo
sudo update-rc.d ryftwebapidemo defaults

echo "  --> OK"

echo ""

# update ownership
echo '---'
echo "Updating ownership to ${aiusername}"
echo '---'

echo ""

sudo chown -R ${aiusername} ~/ryft
sudo chgrp -R ryftone ~/ryft

echo '---'
echo "Edit '${target}/ryft-demo/conf/ryft.properties' to configure the demo site"
sudo service ryftwebapidemo start
echo "Site is started.  Stop/start/restart with 'sudo service ryftwebapidemo [start/stop/restart]'"
echo '---'

echo ""

ENDTIMEAPI=$(date +%s)
echo "The Ryft ONE API web demo installation took $(($ENDTIMEAPI - $STARTTIMEAPI)) seconds to complete."

echo ""

exit 0
