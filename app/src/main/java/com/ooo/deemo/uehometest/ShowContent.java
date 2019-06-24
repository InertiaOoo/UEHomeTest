package com.ooo.deemo.uehometest;

/**
 * Author by Deemo, Date on 2019/5/6.
 * Have a good day
 */
public class ShowContent {

    private String ID;
    private  String content;

    public ShowContent(String ID,String content){
        this.ID = ID;
        this.content = content;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
