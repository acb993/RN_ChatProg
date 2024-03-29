package util;

import java.util.ArrayList;
import java.util.List;

public class Message {

    private int userId;
    private String username;
    private int channelId;
    private List<String> body = new ArrayList<>();


    public Message(int userId, String username, int channelId){
        this.userId=userId;
        this.username=username;
        this.channelId=channelId;
    }

    public void addLine(String line){
        if(line.endsWith("\r\n")){
            body.add(line);
        }else{
            body.add(line+"\r\n");
        }

    }

    public List<String> getBody() {

        if(body.get(body.size()-1)!="EOM\r\n"){
            body.add("EOM\r\n");
        }
        return body;
    }

    public int getChannelId() {
        return channelId;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
