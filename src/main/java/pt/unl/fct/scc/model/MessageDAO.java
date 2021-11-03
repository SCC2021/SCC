package pt.unl.fct.scc.model;

public class MessageDAO {
    private String _rid;
    private String _ts;
    private String user;
    private String channelDest;
    private String body;
    private String mediaId;
    private String responseTo;


    public MessageDAO(Message m){
        this.user = m.getUser();
        this.channelDest = m.getChannelDest();
        this.mediaId = m.getMediaId();
        this.body = m.getBody();
        this.responseTo = m.getResponseTo();
    }
}
