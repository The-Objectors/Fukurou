## Fukurou
A BlueJ plugin that seeks to collect telemetry data from user actions.

## Compiling The Plugin
Before compiling the plugin you may need to install the bluej.jar file into your Maven cache. 
Use the following command in a terminal to tell Maven where to find it!
```
mvn install:install-file -Dfile="lib/bluej.jar" -DgroupId=org.bluej -DartifactId=bluej -Dversion=1.0 -Dpackaging=jar
```
Note: 
- If you are running this command in a powershell terminal you will need to surround both `-DgroupId=org.bluej` and `-Dversion=1.0` in double quotes like so `"-DgroupId=org.bluej"`
