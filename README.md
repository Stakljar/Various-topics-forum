# Various-topics-forum-fullstack

Full stack web application made using Java Spring Boot, ReactJS and MySQL Database.
The application is about people discussing on various topics.
Features include:
- Sign up and Sign in
- JWT authentication and authorization
- E-mail confirmation
- Password change and reset
- Logout
- Browsing discussions by search term or category with pagination
- Adding new discussions
- Commenting on discussions
- Browsing comments with pagination
- Upvoting or downvoting discussions or comments
- Soft deleting comments or discussions with their comments by admin or by the user who posted them
- Browsing user profiles

## How to run
Clone the repository by running this command:
```
git clone https://github.com/Stakljar/Various-topics-forum.git
```

Database setup steps when using MySQL Workbench:
1. Download and run MySQL Server installer from: https://dev.mysql.com/downloads/mysql/
2. Make sure port and root password match the ones defined in Spring Boot's application.properties file during configuration
3. Download and run MySQL Workbench installer from: https://dev.mysql.com/downloads/workbench/
4. Open it and search for local instance, rescan if needed
5. Click on File -> Open SQL Script, look up for db/dump.sql and open it
6. Select lightning icon for running SQL script which will create and populate database
MySQL server needs to be running for you to start backend.

For backend you can use IDEs such as Eclipse with Spring Tools or IntelliJ IDEA and others or you can use command line with Maven.
Steps when using command line with Maven on Windows:
1. Download and run JDK Windows Installer for JDK version 17 or above if JDK is not already installed from: https://www.oracle.com/java/technologies/downloads/
2. Define new environment variable named JAVA_HOME and set its value to jdk location, usually it is like "C:\Program Files\Java\jdk-[version]"
3. In "Path" environment variable add this path "%JAVA_HOME%\bin"
4. Download Apache Maven Binary zip archive from: https://maven.apache.org/download.cgi
5. Extract it and copy extracted file's bin directory path and add it to "Path" environment variable
6. In project's root directory, type in command line:

    ```
    cd backend
    mvn spring-boot:run
    ```

Install node_modules and run frontend, in project's root directory run following:
```
cd frontend
npm install
npm run
```

For using mailing services you need to install and run maildev: https://github.com/maildev/maildev
by running following commands:
```
npm install -g maildev
maildev
```

### Running with Docker
Follow system requirements, download and run Docker Desktop Installer from: https://docs.docker.com/get-docker/ if you don't have Docker Desktop installed already.
After that run following command:
```
docker-compose up
```
You should have your images created and containers up and running.
