package pt.unl.fct.scc.cron;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pt.unl.fct.scc.model.Channel;
import pt.unl.fct.scc.model.Message;
import pt.unl.fct.scc.model.User;
import pt.unl.fct.scc.service.ChannelService;
import pt.unl.fct.scc.service.MessageService;
import pt.unl.fct.scc.service.UserService;

import java.util.List;
import java.util.logging.Logger;

@Service
public class CronDeleter {

    @Autowired
    MessageService messageService;

    @Autowired
    ChannelService channelService;

    @Autowired
    UserService userService;

    Logger logger = Logger.getLogger(this.getClass().toString());

    @Scheduled(fixedRate = 120000L) // every 120 s
    private void deleterMessages(){
        logger.info("CRON DELETE MESSAGES");

        List<Message> deleted = messageService.getDeleted();
        List<Channel> channels = channelService.getChannels();

        for (Channel ch: channels) {
            List<Message> messageList = ch.getMessageList();
            boolean edited = false;
            for (Message m: messageList) {
                for (Message mm: deleted) {
                    if (m.getMessageID().equals(mm.getMessageID())){
                        messageList.remove(m);
                        edited = true;
                    }
                }
            }
            if (edited){
                ch.setMessageList(messageList);
                channelService.updateChannel(ch);
            }

            messageService.purgeMessages();
        }
    }

    @Scheduled(fixedRate = 120000L) // every 120 s
    private void deleterChannels(){
        logger.info("CRON DELETE CHANNELS");

        List<Channel> deleted = channelService.getDeleted();
        List<User> users = userService.getUsers();

        for (User user: users) {
            List<String> channelIds = user.getChannelIds();
            boolean edited = false;
            for (String ch: channelIds) {
                for (Channel channel: deleted) {
                    if (ch.equals(channel.getChannelID())){
                        channelIds.remove(ch);
                        edited = true;
                    }
                }
            }
            if (edited){
                user.setChannelIds(channelIds);
                userService.updateUser(user);
            }

            channelService.purgeChannels();
        }
    }
}
