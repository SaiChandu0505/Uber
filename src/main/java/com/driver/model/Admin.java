package com.driver.model;

import javax.persistence.*;

@Entity
@Table(name = "admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int adminId;

    String username;

    String password;

    public Admin(){

    }
    public Admin(String userName, String password) {
        this.username = userName;
        this.password = password;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminid) {
        this.adminId = adminid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}