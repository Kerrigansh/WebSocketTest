package com.hao.websocket1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class DBPushSingletonBean {
    private Thread thread = null;
    private ServerSocket server = null;
    @EJB
    private JMSSenderSessionBean jmsService;

    @PostConstruct
    public void init() {
        startServer();
        Logger.getLogger(DBPushSingletonBean.class.getName()).log(Level.INFO, "DBPush Server start");
    }

    @PreDestroy
    public void remove() {
        stopServer();
        Logger.getLogger(DBPushSingletonBean.class.getName()).log(Level.INFO, "DBPush Server end");
    }

    private void startServer() {
        thread = new Thread(new Runnable() {
            public void run() {
                try {
                    server = new ServerSocket(8800);
                    while (!thread.isInterrupted()) {
                        Socket client = server.accept();
                        char[] buffer = new char[64];
                        StringBuilder sb = new StringBuilder();
                        Reader in = new InputStreamReader(client.getInputStream(), "UTF-8");
                        while (true) {
                            int rsz = in.read(buffer, 0, buffer.length);
                            if (rsz < 0) {
                                break;
                            }
                            sb.append(buffer, 0, rsz);
                        }
                        String msg = sb.toString();
                        jmsService.sendMessage(msg);
                        Logger.getLogger(DBPushSingletonBean.class.getName()).log(Level.INFO, "DBPush msg\n" + msg);
                    }
                } catch (IOException e) {
                    Logger.getLogger(DBPushSingletonBean.class.getName()).log(Level.SEVERE, null, e);
                } catch (Throwable t) {
                    Logger.getLogger(DBPushSingletonBean.class.getName()).log(Level.SEVERE, null, t);
                }
            }
        });
        thread.start();
    }

    private void stopServer() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                Logger.getLogger(DBPushSingletonBean.class.getName()).log(Level.SEVERE, null, e);
            }
            server = null;
        }
    }
}
