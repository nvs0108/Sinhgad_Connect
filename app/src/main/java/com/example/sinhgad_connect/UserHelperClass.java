package com.example.sinhgad_connect;

import android.os.Parcel;
import android.os.Parcelable;

public class UserHelperClass implements Parcelable {
    private final String name;
    private final String username;
    private String email;
    private final String phoneNo;
    private String password;
    private boolean isAdmin;

    public UserHelperClass(String name, String username, String email, String phoneNo, String password, boolean isAdmin) {
        this.name = name;
        this.username = username;
        setEmail(email);
        this.phoneNo = phoneNo;
        setPassword(password);
        this.isAdmin = isAdmin;
    }

    protected UserHelperClass(Parcel in) {
        name = in.readString();
        username = in.readString();
        email = in.readString();
        phoneNo = in.readString();
        password = in.readString();
        isAdmin = in.readByte() != 0;
    }

    public static final Creator<UserHelperClass> CREATOR = new Creator<UserHelperClass>() {
        @Override
        public UserHelperClass createFromParcel(Parcel in) {
            return new UserHelperClass(in);
        }

        @Override
        public UserHelperClass[] newArray(int size) {
            return new UserHelperClass[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (isValidEmail(email)) {
            this.email = email;
        } else {
            // Handle invalid email situation
        }
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (isValidPassword(password)) {
            this.password = password;
        } else {
            // Handle invalid password situation
        }
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 4;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(phoneNo);
        dest.writeString(password);
        dest.writeByte((byte) (isAdmin ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
