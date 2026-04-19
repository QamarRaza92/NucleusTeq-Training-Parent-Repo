package com.springadvanced.taskmanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceClient
{
    private static final Logger log = LoggerFactory.getLogger(NotificationServiceClient.class);
    public String sendNotification(String message)
    {
        log.info("NotificationService called");
        return message;
    }
}