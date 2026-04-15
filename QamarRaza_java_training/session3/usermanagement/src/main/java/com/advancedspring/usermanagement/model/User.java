package com.advancedspring.usermanagement.model;

public class User 
{
    private Integer id;
    private String name;
    private Integer age;
    private String gender;
    private String email;

    //Create public constructor to set data
    public User(Integer id,String name,Integer age,String gender,String email)
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
    public Integer getId(){return id;}
    public String getName(){return name;}
    public Integer getAge(){return age;}
    public String getGender(){return gender;}
    public String getEmail(){return email;}
}