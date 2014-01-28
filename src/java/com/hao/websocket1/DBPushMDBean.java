/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hao.websocket1;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.websocket.Session;


@JMSDestinationDefinition(
        name = "java:app/jms/webappQueue",
        interfaceName = "javax.jms.Queue")
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:app/jms/webappQueue")
})
public class DBPushMDBean implements MessageListener {
    
    @EJB
    private MyWSEndpoint endpoint;
    
    public DBPushMDBean() {
    }
    
    @Override
    public void onMessage(Message message) {
        try {
            String msg = message.getBody(String.class);
            System.out.println("mdb onMessage\n" + msg);
            endpoint.sendToAll(msg);
        } catch (JMSException e) {
            Logger.getLogger(DBPushMDBean.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
}
