package cn.encmed.websockettest.models;

public class TupperIO {

    private String uid;
    private String day;

    public TupperIO(String uid,String day) {
        this.uid = uid;
        this.day = day;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

}
