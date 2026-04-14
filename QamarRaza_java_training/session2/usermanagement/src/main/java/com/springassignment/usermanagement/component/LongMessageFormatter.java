package com.springassignment.usermanagement.component;

import org.springframework.stereotype.Component;

@Component
public class LongMessageFormatter
{
    public String getMessage()
    {
        return "This is a long formatted and detailed message with more details.";
    }
}