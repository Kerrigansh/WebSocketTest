package com.hao.websocket1;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/stock")
@Singleton
public class MyWSEndpoint implements Serializable {

    private static final long serialVersionUID = 42L;
    private static final Logger logger = Logger.getLogger(MyWSEndpoint.class.getName());
    private Session session = null;
    private Thread thread = null;
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());

    public MyWSEndpoint() {}

    @OnOpen
    public void open(Session session, EndpointConfig conf) {
        logger.log(Level.INFO, "ws onOpen");
        sessions.add(session);
        this.session = session;
        session.getAsyncRemote().sendText("Hello WebSocket Client - from server hc: " + this.hashCode());
    }

    @OnMessage
    public void message(String message, Session session) {
        logger.log(Level.INFO, "ws onMessage");
        switch (message.toUpperCase()) {
            case "START":
                System.out.println("====> case start");
                //startPush();
                break;
            case "END":
                System.out.println("====> case end");
                //stopPush();
                break;
            default:
                session.getAsyncRemote().sendText("echo" + message + "back");
                break;
        }
    }

    @OnError
    public void error(Session session, Throwable error) {
        logger.log(Level.SEVERE, "ws onError", error);
        try {
            stopPush();
            session.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "error in closing socket");
        }
    }

    @OnClose
    public void close(Session session, CloseReason reason) {
        logger.log(Level.INFO, "ws onClose with code: " + reason.getCloseCode());
        sessions.remove(session);
        try {
            stopPush();
            session.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "error in closing socket");
        }
    }

    public void sendToAll(String msg) {
        if(sessions.isEmpty()) {
            System.out.println("ERROR current session is null!");
            return;
        }
        for(Session s : sessions) {
            s.getAsyncRemote().sendText(msg);
        }
    }
    
    public void printSessionSta() {
        if(session == null) {
            System.out.println("current session is null");
            return;
        }
        System.out.println("current session: " + session.hashCode());
        for(Session s : session.getOpenSessions()) {
            System.out.println("open sessions: " + s.hashCode());
        }
    }

    private void startPush() {
        thread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (!thread.isInterrupted()) {
                        String text = "tmp str";
                        if (text == null) {
                            text = "empty";
                        }
                        for (Session s : session.getOpenSessions()) {
                            s.getAsyncRemote().sendText(text);
                        }
                    }
                } catch (Throwable t) {
                    logger.log(Level.SEVERE, null, t);
                }
            }
        });
        thread.start();
    }

    private void stopPush() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }
}
