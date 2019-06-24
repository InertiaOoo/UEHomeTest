package com.ooo.deemo.uehometest;

public class TestLog {
    private String ID;
    private  String logmsg;

    public TestLog(String ID,String logmsg){
        this.ID = ID;
        this.logmsg = logmsg;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getLogmsg() {
        return logmsg;
    }

    public void setLogmsg(String logmsg) {
        this.logmsg = logmsg;
    }
}
