package scc.models;

public class Message {
    private String id;
    private String responseTo;
    private String channelDest;
    private String user;
    private String body;
    private String mediaId;

    public String getId() {
        return id;
    }

    public String getResponseTo() {
        return responseTo;
    }

    public String getChannelDest() {
        return channelDest;
    }

    public String getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public String getMediaId() {
        return mediaId;
    }
}
