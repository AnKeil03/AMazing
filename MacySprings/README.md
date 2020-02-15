Setting Up Application:

1. Assumed install of MySQL, Java, and DBeaver is already done

2. Open SQL Script "seproject7.sql" in DBeaver (found in the db Dir)

3. Execute script in DBeaver

4. Configure Gradle by running gradle in IDE

5. Configure Spring by running user.sql in DBeaver (found in the db Dir)

6. Run Main() in Application Class

7. To Test (Until automated testing is established) in a command prompt enter "
curl localhost:8080/user/add -d name = testing -d email = test@someemail.com"
If Successfully set up this will return "Successfully added user"