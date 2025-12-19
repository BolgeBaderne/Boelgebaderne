package com.example.bolgebaderne.service;

import com.example.bolgebaderne.model.SaunaEvent;
import com.example.bolgebaderne.model.User;
import org.springframework.stereotype.Service;

@Service
public class NotificationService
{
    // Simpel notifikation ved forfremmelse fra venteliste
    public void sendWaitlistPromotion(User user, SaunaEvent event)
    {
        // Simpel version: bare log til konsollen
        System.out.println(
                "[NOTIFICATION] Bruger " + user.getEmail() +
                        " har fået en plads på event '" + event.getTitle() +
                        "' (id=" + event.getEventId() + ")"
        );
    }
}
