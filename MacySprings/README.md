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



How to Run Application on Mac:

0. installations required: MySQL, Java, JDK, gradle, npm, node
1. execute SQL scripts found in "seproject7.sql" and "user.sql" to set up MySQL database on localhost with associated user

** OS-Specific Modifications **

2. at the bottom of build.gradle:
    change:     processResources.dependsOn 'buildReactApp'
    to          processResources.dependsOn 'buildReactAppMacOS'
    * uses forward slashes instead of backslashes for directory structure

3. open terminal, cd to "MacySprings" directory, type: gradle build

4. once build completes, type: gradle run

5. test in a new terminal window by typing:
    curl localhost:8080/user/add -d name="testing" -d email="test@someemail.com"




MODIFICATIONS FROM MASTER BRANCH:

1. added to build.gradle: buildReactAppMacOS
    * replaces two backslashes with one fwd slash in directory structures
    * necessary modification for this operating system
    (requires processResources.dependsOn to be 'buildReactAppMacOS'
     instead of just 'buildReactApp' at the bottom of build.gradle)

2. added the following dependency to build.gralde:
	testCompile "junit:junit:4.12"
    * fixed issue where junit library couldn't be found


3. in src/main/resources/application.properties:
	change:    spring.datasource.password=yes
	to:        spring.datasource.password=S0m3!p@ssw0rd
    * fixed unable to connect to database issue
    * fixed unit test invalid state issue

4. add to plugins in build.gradle:
	id 'application'
    * fixed issue of "task run not found" when attempting
      to call 'gradle run' after a successful 'gradle build'

5. add the application plugin in build.gradle:
application {
    mainClassName = 'com.seProject.groupProject7.Application'
}
    * fixed same issue as (4); both changes required for fix


6. changed curl test in readme by adding quotes around values:
    curl localhost:8080/user/add -d name="testing" -d email="test@someemail.com"
    * fixed "could not resolve host" issue
