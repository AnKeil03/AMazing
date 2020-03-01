Setting Up Application:

1. Assumed install of MySQL, Java, and DBeaver is already done

2. Open SQL Script "seproject7.sql" in DBeaver (found in the db Dir)

3. Execute script in DBeaver
3.a. If the script does not run in DBeaver, open up the MySQL terminal to execute code.

4. Configure Gradle by running gradle in IDE

5. Configure Spring by running user.sql in DBeaver (found in the db Dir)
5.a. If the script does not run in DBeaver, open up the MySQL terminal to execute code.

6. Run Main() in Application Class

7. To Test (Until automated testing is established) in a command prompt enter "
curl localhost:8080/user/add -d name = testing -d email = test@someemail.com"
If Successfully set up this will return "Successfully added user"

8. To test the get command, enter in command prompt "curl localhost:8080/user/get -d name=testing"
If Successful, the command will return the previously added user.

9. To test the get all command, enter in command prompt "curl localhost:8080/user/all"
If successful, the command will return all users in the DB (currently only one)

Debugging:
-Check to see that the username and password for the database scripts and application.properties are the same.
-If the setup configurations from Gradle don't work, manually install npm and node.
-If npmInstall task fails, submit an issue, but it's likely a problem with path.
