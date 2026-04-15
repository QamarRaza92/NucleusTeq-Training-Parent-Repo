package com.advancedspring.usermanagement.model;

public class User 
{
    private int id;
    private String name;
    private int age;
    private String gender;
    private String email;

    //Create public constructor to set data
    public User(int id,String name,int age,String gender,String email)
    {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.email = email;
    }

    //Create No-Args constructor
    public User(){}

    //Create getter methods
    public int getId(){return id;}
    public String getName(){return name;}
    public int getAge(){return age;}
    public String getGender(){return gender;}
    public String getEmail(){return email;}
}