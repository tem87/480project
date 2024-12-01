First begin by downloading all the files in Group07java.zip
You should see 4 folders, Boundary, Database, Domain and Visual.

This project will require the use of MySQL. In order to get it to work
ensure that you download the Group07config.zip file. 
In this folder there should be the mysql-connector-j-9.1.0.jar
Depending on your IDE/machine, set up the necessary path so that it 
the project can recognize it. 

Now download the sql file. It should be called Group07.sql.
Run it on MySQL.

Now in order for the project to work with your machine, 
go to the Database folder, and change the following lines to your information. 

    private static final String URL = "jdbc:mysql://localhost:3306/MovieAppDB";
    private static final String USER = "root";
    private static final String PASSWORD = "YOUR_PASSWORD.";


To run the code, run MainApp located in the Domain Folder. 

To test for Admin Login, the credentials are
Username: admin
Password: admin12345

Note that the database is populated with test values that you can use,
including vouchers, users, registeredUsers. 

