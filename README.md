This project is a Web Interface and REST API to demonstrate the capabilities of the Ryft API. 

## Building and deploying the server on Ubuntu

### Using the Install script

    curl -v -u 'your github username' -H 'Accept: application/vnd.github.v3.raw' -L https://api.github.com/repos/getryft/apidemo/contents/ryft-demo/bin/install.sh | sh
    sudo service ryft-demo start
    
The script will automatically:
* install Git, Java and Maven if they are not already installed
* Clone the repository or pull the latest changes (if SSH keys are not configured, it will prompt for username/password)
* Compile and install under /opt
* Set-up upstart

To avoid the password prompt when downloading the script, you can create an access token following [these instructions][13] and the use the following curl command:

    curl -v -H 'Authorization: token INSERTYOURTOKEN' -H 'Accept: application/vnd.github.v3.raw' -L https://api.github.com/repos/getryft/apidemo/contents/ryft-demo/bin/install.sh | sh

### Manually

Install git:

    sudo apt-get install git

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

Once this has been done once, to quicky re-deploy with the latest changes from Git, run from the 'apidemo' folder 

    ./ryft-demo/bin/redeploy.sh

### Configuration

The ryft.properties contains details on each property to configure. There are at least 2 values that should be updated:
* ryft.fs.root for the root of the Ryft file system where input and output files are
* ryft.workingDir for the working directories where C program will be generated

### Logs

The logs contain by default the Query ID so one can easily figure out what happened and replicate the issue by manually executing commands in the working directory.
The query ID is used as the working directory where the C program is generated and compiled (under ryft.workingDir). If the program fails to compile or execute, one can check the C program generated (main.c) or try to re-issue the compilation commands:

    /usr/bin/gcc -Wextra -g -Wall -L/usr/lib/x86_64-linux-gnu/ -c -o main.o main.c
    /usr/bin/gcc main.o -o ryft_demo -lryftone
    
Or execute the program

    ./ryft_demo
    
Or check the standard output and error logs of the command (stdout.log and stderr.log).


## Making calls to the REST API

The APIs are deployed under /api in the same server as the web interface. There are 2 end points:
* Ryft API
* File API to retrieve the result and index files

### Ryft API

The Ryft API allows to execute one of the 4 primitives (search, fuzzy search, term frequency and sort) against the Ryft box.
It's a POST request taking a JSON document describing the query to execute.

There are 2 mandatory attributes:
* type: the type of query: search, fuzzy, sort or term
* input: a comma-separated list of input files

The other attributes depend on the type of query and correspond to the parameters defined in the [Ryft API documentation][7].

For a search, the following attributes are required:
* searchQuery: the search query, ex: (RAW_TEXT CONTAINS "555")
* searchWidth: the surrounding width

For a fuzzy search:
* fuzzyQuery: the search query, ex: (RAW_TEXT CONTAINS "555")
* fuzzyWidth: the surrounding width
* fuzziness: the fuzziness

For a term frequency query:
* termField: the field to extract terms from
* termFormat: the format to use to interprete the data

For a sort query:
* sortField: the field to sort on
* sortOrder: ASC or DESC

There are additional optional parameters:
* output: specifies the name of the output file, one will be generated if not specified
* writeIndex: true or false to indicate whether or not to generate the index file for search and fuzzy search queries
* nodes: the number of nodes to use (2 by default)

Example query for a search:

    POST /ryftone
    content-type: application/json
    accept: application/json
    
    {
        "type" : "search",
        "input" : "passengers.txt",
        "searchQuery" : "(RAW_TEXT CONTAINS \"555\")",
        "searchWidth" : 10
    }

The response is also a JSON document with the list of statistics returned by the Ryft API as well as the location of the output and index file:

    {
        "statistics" : [{
            "name" : "START_TIME",
            "value" : 1422492832169
        },
        {
            "name" : "EXECUTION_DURATION",
            "value" : 204
        },
        {
            "name" : "TOTAL_BYTES_PROCESSED",
            "value" : 574423
        },
        {
            "name" : "TOTAL_NUMBER_OF_MATCHES",
            "value" : 236201
        }],
        "outputFile" : "demo_output_1422492832169",
        "indexFile" : null
    }

### File API

The Ryft API response contains the path to the output and index files, which can then be retrieved with the file API.

Example:

    GET /file?file=/demo_output_1422492832169
    
All file paths will start by '/', it doesn' denote the root of the fily system where the server is running, but the root of the Ryft file system, which can be anywhere on the server's file system.

This API also exposes a /file/browse end point used by the server-side file browser component of the web interface. This is required to select the input dataset for the Ryft calls. It returns an HTML list of files and directories under the requested path.   


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
For people familiar with Spring and Wicket there shouldn't be anything confusing, all is rather straightforward.

For people less familiar here's an attempt to walk through the code and how a few things magically happen thanks to Spring.

### Entry point

The entry point of the program is the DemoApplication class under the com.metasys.ryft package, which will start an embedded Jetty server. The server will read the web descriptor (web.xml file) and start the server accordingly.
The web.xml file is under src/main/webapp/WEB-INF and defines:
* A Listener to load the log4j configuration
* A Listener to load the Spring context (spring-config.xml under src/main/resources, which is available from the classpath)
* The declaration of the Spring Dispatch Servlet, which will handle all the API call requests. The matching servlet mapping restrict the usage of this Servlet to /api, so only requests made to the server under /api will be handled by the Servlet. The dispatch servlet defines its own Spring context, which is the "Web Application Context". Here it points to an empty file as we don't have any web-specific ressources, everything is declared in the 'Application Context' described with spring-config.xml.
* The declaration of the Wicket filter, mapped to /demo. The filter is configured to work with Spring so that beans can be injected in Wicket components. The 'applicationBean' parameter value matches the name of the Spring bean that should be used as the Wicket web application: com.metasys.ryft.DemoApplication. 

There's also an index.html file under src/main/webapp, which will be the home page when navigating through a browser, redirecting to /demo to get to the Wicket home page.

With this, when Jetty load the web.xml file, Spring will automatically load the context using the spring-config file and Wicket will configure itself.

### Spring

Spring is configured to work with annotations and discover beans to create by scanning the package com.metasys.ryft, making the configuration minimal. The only configuration we need to declare is the JSON message converter so Spring can understand REST requests with a JSON body and transform objects in the response into JSON.
All the components that we need to load via Spring declare a @Component annotation (like com.metasys.ryft.DemoApplication).  

Spring MVC will automatically handle REST requests received under /api and dispatch them to the right component. The 2 APIs (under com.metasys.ryft.api) define an @Controller annotation indicating to Spring that they are able to handle REST calls and @RequestMapping defining which requests they can handle (/ryftone or /file).

### Wicket

Each Wikcet component in Java has a matching HTML file, stored under the html folder, with the same package structure as Java. The HTML files may have a matching .properties file, defining localized resources (in English only just to map technical names to user-friendly text).  
The web resources (CSS, Javascript, images...) are under the src/main/webapp folder.
The Wicket application is just one page: DemoPage under com.metasys.ryft.web. It contains all the components and input fields to describe a query.

The page uses some components from [Wicket jQuery UI][8]. For instance the feedback panel, where info and error messages are displayed uses the Kendo feedback panel. 

The page contains a form with all the input fields, some of which have custom validation. Once the form is validated, the ExecuteButton (com.metasys.ryft.web.component.ExecuteButton) is responsible for submitting the query to the API, which will then submit to the Ryft box.  


### C Program

The Wicket interface relies on the API to execute a Ryft query, and the API relies on the ProgramManager class (com.metasys.ryft.program.ProgramManager) to actually execute the query against the Ryft box.
The ProgramManager generates a C program based on the parameters of the query, compiles it, then execute it. When the program is done, it can read back a log file with the standard output of the C program to extract the statistics, or any error message. 

Each call is assigned an ID by the API, which is used as the name of the working directory. The working directory will contain the following files after a successful execution:
* main.c the generated C program
* main.o and ryft_demo the product of the compilation
* stdout.log and stderr.log, respectively containing the standard output and error of the commands executed

### Packaging and Maven

All the dependencies are manages with Maven. On top of Spring, Wicket and Jetty the project also relies on 
* commons-lang and commons-io for convenience
* [Jackson][9] to read/write JSON objects
* [Log4j2][10] for logging
* [JUnit][11] and [JMockit][12] for unit tests

Maven includes the HTML folder in the resources so it is available from the classpath for Wicket.

And finally the assembly.xml describes how to build a tar.gz file with everything required to deploy the server. The generated assembly contains:
* lib folder with all dependencies
* webapp folder with all the web resources (including the WEB-INF/web.xml descriptor)
* conf with the log4j2.xml and ryft.properties file so they can easily be modified


[1]: http://www.oracle.com/technetwork/articles/javase/index-jsp-138363.html
[2]: http://git-scm.com/book/en/v2/Getting-Started-Installing-Git
[3]: http://maven.apache.org/download.cgi
[4]: https://www.eclipse.org/downloads/
[5]: http://spring.io/
[6]: https://wicket.apache.org/
[7]: https://github.com/getryft/apidemo/blob/master/ryft-demo/doc/Ryft_ONE_Library_API_Users_Guide.pdf
[8]: http://www.7thweb.net/wicket-jquery-ui/
[9]: http://wiki.fasterxml.com/JacksonHome
[10]: http://logging.apache.org/log4j/2.x/
[11]: http://junit.org/
[12]: http://jmockit.github.io/
[13]: https://help.github.com/articles/creating-an-access-token-for-command-line-use/
