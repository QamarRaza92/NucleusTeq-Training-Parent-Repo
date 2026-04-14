package com.springassignment.usermanagement.model;

public class User {
    private int id;
    private String name;
    private int age;
    private String email;

    // Constructor
    public User(int id, String name, int age, String email) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
    }

    //No Args Constructor
    public User(){}

    // Getters (Required for Spring to show data in JSON)
    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getEmail() { return email; }
}