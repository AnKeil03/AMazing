package com.seProject.groupProject7;

import javax.persistence.*;

@Entity // This tells Hibernate to make a table out of this class
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    private String name;
    private String password;
    private String email;
    private int userSession;

    public User() {
        this.name="testuser";
        this.email="test@email.com";
        this.password = "testpassword";
        this.userSession = -1;
    }

    public User(String name, String password, String email) {
        this.name=name;
        this.email=email;
        this.password = password;
        this.userSession=-1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String newp) {
        this.password=newp;
    }

    public int getUserSessionID() {return this.userSession;}
    public void setUserSessionID(int id) {this.userSession=id;}

    public String getPassword() {
        return this.password;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(this.id);
        sb.append(", password (decrypted)=").append(this.password);
        sb.append(", name='").append(this.name).append('\'');
        sb.append(", email=").append(this.email);
        sb.append('}');
        return sb.toString();
    }
}
