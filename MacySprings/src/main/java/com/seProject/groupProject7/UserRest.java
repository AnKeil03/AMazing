package com.seProject.groupProject7;

public class UserRest {
    private final String name;
    private final String email;
    private final int resultCode;
    private final String result;
    private final String pword;

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
    public String getPassword() {return pword;}

    public UserRest(String name, String pw, String email) {
        this(name, pw, email, "Success", 0);
    }

    public UserRest(String name, String pw, String email, String result, int resultCode) {
        this.name = name;
        this.email = email;
        this.pword = pw;
        this.result =  result;
        this.resultCode = resultCode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append(", name='").append(this.name).append('\'');
        sb.append(", password=").append(this.pword);
        sb.append(", email=").append(this.email);
        sb.append('}');
        return sb.toString();
    }
}


