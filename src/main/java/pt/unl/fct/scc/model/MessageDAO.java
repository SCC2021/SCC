package pt.unl.fct.scc.model;

public class MessageDAO {
    private String _rid;
    private String _ts;
    private String sender;
    private String channelDest;
    private String body;
    private String[] media;
    private String responseTo;


    public MessageDAO(Message m){
        this.sender = m.getSender();
        this.channelDest = m.getChannelDest();
        this.media = m.getMedia();
        this.body = m.getBody();
        this.responseTo = m.getResponseTo();
    }
}
