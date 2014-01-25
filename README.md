Chaturaji Four Player Chess
===========================

Android application for Chaturaji - a four player chess-like game meaning "four kings"

This project is built using Maven, for the following reaons:
* each developer can choose their own IDE (such as Eclipse or IntelliJ) as they prefer
* the build process will create two artifacts: an android application and a web application
* the build process will automatically run any tests added to the project
* application libaries (and the libaries used by libaraies) will be automatically managed and downloaded

For an introduction to Maven see [Maven in 5 Minutes](http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)

To compile project:
 1. Install [mvn](http://maven.apache.org/)
 2. mvn clean install

To add a new libary (in addition to the stanard Java JDK):
 1. Go to http://search.maven.org/
 2. Search for the libaray you want to use, it may help to look at the website for the libaray to know what to search for
 3. Click on the version you want and find the section on the page that looks like the follow:

FUCK

    <dependency>
        <groupId>org.apache.httpcomponents</groupId>
        <artifactId>httpcomponents-client</artifactId>
        <version>4.3.2</version>
    </dependency>
