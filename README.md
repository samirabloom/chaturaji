Chaturaji Four Player Chess
===========================

Android application for Chaturaji - a four player chess-like game meaning "four kings"

####This project is built using Maven, for the following reasons:
* for Java applications Maven is the most common and widely used tool for compilation and library management; it is the Java equivalent of Make for C++
* each developer can choose their own IDE (such as Eclipse or IntelliJ) as they prefer as all Java IDEs support Maven
* the build process will create two artifacts: an android application and a web application
* the build process will automatically run any tests added to the project
* application libraries (and the libraries used by libraries) will be automatically managed and downloaded

For an introduction to Maven see [Maven in 5 Minutes](http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)

##To compile project:
 1. Install [mvn](http://maven.apache.org/)
 2. Add the bin directory inside the location where you unzipped your maven download to your **PATH** environment variable, in the terminal as follows:

--------------------

    export PATH=$PATH:~/Downloads/apache-maven-3.1.1-bin/bin
    echo "export PATH=$PATH:~/Downloads/apache-maven-3.1.1-bin/bin" >> ~/.bash_profile
    echo $PATH

--------------------

**Note:** *~/Downloads/apache-maven-3.1.1-bin* must be replaced by the location where you unzipped your maven download.

 3. Install [Android SDK](http://developer.android.com/sdk/index.html)
 4. Set **ANDROID_HOME** environment variable to point to sdk folder, in the terminal as follows:

--------------------

    export ANDROID_HOME=~/Downloads/adt-bundle-mac-x86_64-20131030-2/sdk
    echo "export ANDROID_HOME=~/Downloads/adt-bundle-mac-x86_64-20131030-2/sdk" >> ~/.bash_profile
    echo $ANDROID_HOME

--------------------

**Note:** *~/Downloads/adt-bundle-mac-x86_64-20131030-2* must be replaced by the location where you downloaded the Android SDK.

 5. Create android emulator, in the terminal as follows (or plugin an Android device via USB):

--------------------

    $ANDROID_HOME/tools/android avd

--------------------

 7. Start an Android emulator using the Android Virtual Device Manager
 6. Run the build, in the terminal as follows:

--------------------

    mvn clean install

--------------------

##To add a new library:
 1. Go to http://search.maven.org/
 2. Search for the library you want to use, it may help to look at the website for the library to know what to search for
 3. Click on the version you want and find the section on the page that looks like the follow:

--------------------

    <dependency>
        <groupId>org.apache.httpcomponents</groupId>
        <artifactId>httpcomponents-client</artifactId>
        <version>4.3.2</version>
    </dependency>
    
--------------------

 4. Add this section to the pom.xml file as follows:

--------------------

    <dependencies>
    
        ...
        
        <!-- Apache HTTP Client -->
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpcomponents-client</artifactId>
            <version>4.3.2</version>
        </dependency> 
        
        ...
        
    </dependencies>
    
--------------------

**Note:** The Java JDK standard API is automatically added to Maven projects so you only need to add additional libraries such as Apache Http Client or the Android JDK.

## Useful Commands

$ANDROID_HOME/platform-tools/adb devices

## Add SSL (HTTPS) Support To Tomcat

 1. Edit server.xml inside the **conf** directory in the tomcat installation location to add the following section:

-----
    <!-- Define a SSL Coyote HTTP/1.1 Connector on port 8443 -->
    <Connector
        protocol="HTTP/1.1"
        port="8443" maxThreads="200"
        scheme="https" secure="true" SSLEnabled="true"
        keystoreFile="<chaturaji project root>/keystore" keystorePass="changeit"
        clientAuth="false" sslProtocol="TLS"/>
-----

For more information see [tomcat instruction](http://tomcat.apache.org/tomcat-8.0-doc/ssl-howto.html)


## How to install MySQL to compile the latest DAO code:
 
 
 1. First install MySQL typing this in the terminal
 

--------------------

    sudo apt-get install mysql-server

--------------------


 2. Install mysql client, server and the jdbc connector:
 

--------------------

    sudo apt-get install mysql-server
    sudo apt-get install mysql-client
    sudo apt-get install libmysql-java

--------------------

Then you can do mvn clean install as usual
     
