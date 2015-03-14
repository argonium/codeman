# CodeMan
CodeMan is a Java GUI (Swing) application that makes it easy to manage zip files, so you can search the contents of files in the zip file and view the files and search results.

To prepare a zip file for use by CodeMan, select File | Install Zip... to select a local zip or JAR file.  The contents will be displayed in one panel, with a console available in a separate panel.

The console provides the following commands:

* cat file <file>
* cat result #
* clear
* date <number>
* debug <on | off>
* exit
* help
* help <start of a command>
* list files
* list files <query>
* open file <file>
* open result # - open a result from search, list endpoints or list files
* query result # - print info on the result number
* quit
* search <query>
* searchex - open a Search dialog
* version
* zip info
* 
The application uses the Lucene and RSyntaxTextArea libraries.

To build the application, use Ant to run the command 'ant clean dist'.  This will produce codeman.jar.  To run it, copy the JAR files in the libs/ directory into the same directory as codeman.jar, and then use 'java -jar codeman.jar' to run the application, or double-click on codeman.jar from the desktop.

The source code is released under the MIT license.
