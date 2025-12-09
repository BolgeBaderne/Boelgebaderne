package com.example.bolgebaderne.model;

import java.time.LocalDateTime;

public class InformationsHub
{
    private int infoId;
    private String title;
    private String content;
    //private InfoType type;
    private LocalDateTime publishedAt;
    private boolean memberOnly;

    public InformationsHub(int infoId, String title, String content, LocalDateTime publishedAt, boolean memberOnly)
    {
        this.infoId = infoId;
        this.title = title;
        this.content = content;
        this.publishedAt = publishedAt;
        this.memberOnly = memberOnly;
    }


    public int getInfoId() {return infoId;}
    public String getTitle() {return title;}
    public String getContent() {return content;}
    public LocalDateTime getPublishedAt() {return publishedAt;}
    public boolean isMemberOnly() {return memberOnly;}

    public void setInfoId(int infoId) {this.infoId = infoId;}
    public void setTitle(String title) {this.title = title;}
    public void setContent(String content) {this.content = content;}
    public void setPublishedAt(LocalDateTime publishedAt) {this.publishedAt = publishedAt;}
    public void setMemberOnly(boolean memberOnly) {this.memberOnly = memberOnly;}
}
