package com.springadvanced.taskmanager.service;

import com.springadvanced.taskmanager.dto.TodoResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceClientTest
{
    private NotificationServiceClient notificationServiceClient = new NotificationServiceClient();

    @Test
    public void sendNotification_ShouldSendNotification()
    {
        //data
        String message = "Test message";

        //act
        String result = notificationServiceClient.sendNotification(message);

        //assert
        assertEquals(message,result);
        System.out.println("'Send Notification when message is received' Test successfull");
    }

    @Test void sendotification_WhenNoData_ShouldReturnNoData()
    {
        //data
        String message = "";

        //act
        String result = notificationServiceClient.sendNotification(message);

        //assert
        assertEquals(message,result);
        System.out.println("'Send Notification when message is empty' Test successfull");
    }

    @Test void sendNotification_WhenDataIsNull_ShouldReturnNoData()
    {
        //data
        String message = null;

        //act
        String result = notificationServiceClient.sendNotification(message);

        //assert
        assertNull(result);

        System.out.println("'Send Notification when message is null' Test successfull");
    }
}
