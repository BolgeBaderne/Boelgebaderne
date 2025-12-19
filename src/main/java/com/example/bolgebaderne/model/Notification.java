package com.example.bolgebaderne.model;

import java.time.LocalDateTime;

public class Notification
{
    private int notificationId;
    private NotificationType type;
    private NotificationChannel channel;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private boolean read;

    public void markAsRead() {
        this.read = true;
    }

    public Notification(int notificationId, String message, LocalDateTime createdAt, LocalDateTime sentAt, boolean read)
    {
        this.notificationId = notificationId;
        this.message = message;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
        this.read = read;
    }

    public int getNotificationId() {return notificationId;}
    public String getMessage() {return message;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getSentAt() {return sentAt;}
    public boolean isRead() {return read;}


    public void setNotificationId(int notificationId) {this.notificationId = notificationId;}
    public void setMessage(String message) {this.message = message;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public void setSentAt(LocalDateTime sentAt) {this.sentAt = sentAt;}
    public void setRead(boolean read) {this.read = read;}
}
