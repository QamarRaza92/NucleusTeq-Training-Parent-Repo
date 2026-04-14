package com.springassignment.usermanagement.controller;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import com.example.demo.service.MessageService;
@RestController
@RequestMapping("/message")
public class MessageController
{
    private final MessageService messageservice;
    MessageController(MessageService messageservice)
    {
        this.messageservice = messageservice;
    }
    @GetMapping
    public String getMessage(@RequestParam String type)
    {
        return messageservice.getMessage(type);
    }
}