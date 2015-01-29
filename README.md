This project is a Web Interface and REST API to demonstrate the capabilities of the Ryft API. 

## Building and deploying the server on Ubuntu

Install git:

    sudo yum install git

Install Java:

    wget --no-check-certificate -c --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u25-b17/jdk-8u25-linux-x64.tar.gz
    tar -xzf jdk-8u25-linux-x64.tar.gz
    sudo mkdir -p /usr/lib/jvm
    sudo mv jdk1.8.0_25 /usr/lib/jvm
    sudo update-alternatives --install "/usr/bin/java" "java" "/usr/lib/jvm/jdk1.8.0_25/bin/java" 2000
    rm -f jdk-8u25-linux-x64.tar.gz

Get Maven:

    wget http://apache.sunsite.ualberta.ca/maven/maven-3/3.2.5/binaries/apache-maven-3.2.5-bin.tar.gz
    tar xzf apache-maven-3.2.5-bin.tar.gz
    export PATH=$PATH:$PWD/apache-maven-3.2.5/bin
    
Clone the repository:

    git clone https://github.com/getryft/apidemo.git
    
Build:

    cd apidemo/ryft-demo
    mvn package -DskipTests

Deploy:

    cd target
    tar xzf ryft-demo-1.0-SNAPSHOT-bin.tar.gz
    cd ryft-demo-1.0-SNAPSHOT
    
Edit conf/ryft.properties and modify as required.
Start the server:

    java -cp 'conf:lib/*' com.metasys.ryft.DemoApplication

Open a browser on port 8989 of your server. 


## Getting set up for development

### Requirements

Java, Git, Maven and your IDE of choice. 

#### Java
Download and install the latest [JDK][1]. The project requires at least Java 7.

#### Git
Install [Git][2]
Then you can clone the repository:

    git clone https://github.com/getryft/apidemo.git

#### Maven
Download [Maven][3] (at least Maven 3.0), extract and add the bin folder to your path.

#### Eclipse
If you don't already have an IDE, you can download [Eclipse IDE for Java Developers][4], it comes with Maven and Git plugins. 


### Import the project in Eclipse

In Eclipse, use the "Import existing Maven projects" (File -> Import -> Maven) to create the project. Maven will take care of properly setting up the classpath and downloading all the required dependencies.
The entry point is com.metasys.ryft.DemoApplication and will by default start an embedded Jetty server on port 8989. 


## Code walkthrough

The project is built around the [Spring framework][5] for dependency injection and the handling of REST calls as well as [Apache Wicket][6] for the web interface.
 



[1]: http://www.oracle.com/technetwork/articles/javase/index-jsp-138363.html
[2]: http://git-scm.com/book/en/v2/Getting-Started-Installing-Git
[3]: http://maven.apache.org/download.cgi
[4]: https://www.eclipse.org/downloads/
[5]: http://spring.io/
[6]: https://wicket.apache.org/
