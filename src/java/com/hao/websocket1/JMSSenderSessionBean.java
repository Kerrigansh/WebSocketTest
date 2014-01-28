package com.hao.websocket1;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.Queue;

@JMSDestinationDefinition(
        name = "java:app/jms/webappQueue",
        interfaceName = "javax.jms.Queue")
@Stateless
public class JMSSenderSessionBean {

    @Inject
    private JMSContext context;
    @Resource(lookup="java:app/jms/webappQueue")
    private Queue queue;
    
    @TransactionAttribute(REQUIRED)
    public void sendMessage(String msg) {
        context.createProducer().send(queue, msg);
    }
}
