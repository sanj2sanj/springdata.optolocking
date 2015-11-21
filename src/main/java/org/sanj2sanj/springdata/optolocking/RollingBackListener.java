package org.sanj2sanj.springdata.optolocking;

import com.google.gson.Gson;
import org.sanj2sanj.springdata.optolocking.domain.Matchable;
import org.sanj2sanj.springdata.optolocking.domain.MatchableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RollingBackListener implements MessageListener {

    static AtomicInteger i = new AtomicInteger();
    @Autowired
    MatchableRepository repository;

    public MatchableRepository getRepository() {
        return repository;
    }

    public void setRepository(MatchableRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onMessage(Message message) {

        // try {
        // System.out.println(message.getIntProperty("JMSXDeliveryCount")
        // + " getJMSRedelivered " + message.getJMSRedelivered() + " "
        // + message);
        //
        // if (i.getAndAdd(1) < 4)
        // {
        // throw new RuntimeException("rollback");
        // }
        //
        // } catch (JMSException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        if (message instanceof TextMessage) {
            try {
                String text = ((TextMessage) message).getText();
                System.out.println("Recieved " + text);

                Gson gson = new Gson();
                repository.save(gson.fromJson(text, Matchable.class));

            } catch (JMSException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        } else {
            throw new IllegalArgumentException(
                    "Message must be of type TextMessage");
        }
    }
}