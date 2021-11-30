package scc.models;

public class MessageDAO {
    private String _rid;
    private String _ts;
    private String user;
    private String channelDest;
    private String body;
    private String mediaId;
    private String responseTo;
    private String id;


    public String get_rid() {
        return _rid;
    }

    public String get_ts() {
        return _ts;
    }

    public String getUser() {
        return user;
    }

    public String getChannelDest() {
        return channelDest;
    }

    public String getBody() {
        return body;
    }

    public String getMediaId() {
        return mediaId;
    }

    public String getResponseTo() {
        return responseTo;
    }

    public String getId() {
        return id;
    }
}
