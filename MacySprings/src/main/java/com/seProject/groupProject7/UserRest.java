package com.seProject.groupProject7;

public class UserRest {
    private final String name;
    private final String email;
    private final int resultCode;
    private final String result;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getResult() {
        return result;
    }

    public UserRest(String name, String email) {
        this(name, email, "Success", 0);
    }

    public UserRest(String name, String email, String result, int resultCode) {
        this.name = name;
        this.email = email;
        this.result =  result;
        this.resultCode = resultCode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append(", name='").append(this.name).append('\'');
        sb.append(", email=").append(this.email);
        sb.append('}');
        return sb.toString();
    }
}


