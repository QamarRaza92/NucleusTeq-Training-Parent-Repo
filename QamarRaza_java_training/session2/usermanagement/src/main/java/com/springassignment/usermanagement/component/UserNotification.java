package com.springassignment.usermanagement.component;
import org.springframework.stereotype.Component;

@Component 
public class UserNotification
{
    public String sendNotification(String name)
    {
        return "Notification sent to "+name;
    }
}