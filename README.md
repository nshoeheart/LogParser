LogParser
=========

A command-line tool that allows ICON log files to be parsed and saved in a local MySQL database.

Setup instructions:

1) Download MySQL Community Server and MySQL Workbench from http://dev.mysql.com/downloads/

2) Setup a username and password to protect your database from being hacked :)

3) Create a table within a database on the MySQL Workbench application according to the LogSchema.ddl file in the same directory as this file.

4) Clone this repository to your local machine

5) Add the driver file included in src/edu/uiowa/dependencies to your classpath. This is done differently based on whether you will be running this program from the command line or within an IDE.

6) Add your MySQL username, password, and database url to the config.properties file located in src/edu/uiowa. The database url should look something like this:

  jdbc:mysql://localhost:{portNumber}/{dbTitle}
  
where the default port number is 3306 and {dbTitle} is simply whatever you named the database containing the log metrics table.

7) Compile and run the project, select the .log file to parse, and let the program run.
