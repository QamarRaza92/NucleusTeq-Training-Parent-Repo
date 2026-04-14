package com.springassignment.usermanagement.service;

import org.springframework.stereotype.Service;
import java.util.*;
import com.example.demo.component.ShortMessageFormatter;
import com.example.demo.component.LongMessageFormatter;
@Service 
public class MessageService
{
    private final ShortMessageFormatter shortmessageformatter;
    private final LongMessageFormatter longmessageformatter;
    
    public MessageService(ShortMessageFormatter shortmessageformatter, LongMessageFormatter longmessageformatter)
    {
        this.shortmessageformatter = shortmessageformatter;
        this.longmessageformatter = longmessageformatter;
    }

    public String getMessage(String type)
    {
        if(type.equalsIgnoreCase("SHORT"))
        {
            return shortmessageformatter.getMessage();
        }

        else if(type.equalsIgnoreCase("LONG"))
        {
            return longmessageformatter.getMessage();
        }

        else
        {
            return "Invalid type !!";
        }
    }
}