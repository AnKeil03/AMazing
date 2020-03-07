package com.seProject.groupProject7;

import javax.persistence.*;

@Entity // This tells Hibernate to make a table out of this class
public class ServerSetting {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    private String name;
    private String value;

    public ServerSetting() {
        this.name="nextSessionToken";
        this.value="0";
    }

    public ServerSetting(String name, String val) {
        this.name=name;
        this.value = val;
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

    public String getValue() {
        return this.value;
    }

    public void setValue(String to) {
        this.value=to;
    }
}
