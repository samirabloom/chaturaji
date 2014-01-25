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
 2. mvn clean install

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
