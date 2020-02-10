package drms.server;

import java.sql.*;

public class DatabaseManager {
    //may implement Runnable but can't tell if this needs to be on separate thread

    private String dburl,dbuser,dbpass;
    private Connection connection;

    public DatabaseManager() throws SQLException {
        dburl = "jdbc:mysql://localhost:3306/drms";
        dbuser = "root";
        dbpass = "admin";
        connection = DriverManager.getConnection(dburl,dbuser,dbpass);
        //test2();
    }

    /* query(q):
        performs generic sql query calling the execute() method
        returns a result set if one exists
     */
    public void query(String q) throws SQLException {
        try (Statement s = connection.createStatement();) {
            if (s.execute(q)) {
                while (s.getResultSet().next()) {
                    //process select results
                    String name = s.getResultSet().getString("name");
                    String pass = s.getResultSet().getString("password");
                    String email = s.getResultSet().getString("email");
                    int rights = s.getResultSet().getInt("rights");
                    System.out.println(name+" has password "+pass+" and email "+email+" with rights="+rights);
                }
            }
        } catch (SQLException e) {
            System.out.println("query:SQL Error: "+e.getMessage());
        }
    }

    /* queryUpdate(q):
        execute a query that updates a table in database
        returns the number of records affected
     */
    public int queryUpdate(String q) throws SQLException {
        try (Statement s = connection.createStatement();) {
            return s.executeUpdate(q);
        } catch (SQLException e) {
            System.out.println("query:SQL Error: "+e.getMessage());
        }
        return -1;
    }



    public void test2() throws SQLException {
        query("select * from users");
        queryUpdate("INSERT INTO users(name,password,email,rights) VALUES (\"sam\",\"admin1\",\"sam@email.com\",0);");
        query("select * from users");
    }


}