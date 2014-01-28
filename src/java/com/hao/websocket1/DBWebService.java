package com.hao.websocket1;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.sql.DataSource;


@Stateless
@WebService(serviceName = "DBWebService")
public class DBWebService {

    @Resource(lookup="jdbc/smsadmin")
    private DataSource ds;
    
    @WebMethod(operationName = "changeName")
    public String changeName(@WebParam(name = "name") String name) {
        Logger.getLogger(DBWebService.class.getName()).log(Level.INFO, null, "in para: " + name);
        Connection con = null;
        try {
            con = ds.getConnection();
            con.createStatement().executeUpdate("update test set name = '" + name + "' where id = 1");
            return "SUCCESS";
        } catch (SQLException e) {
            Logger.getLogger(DBWebService.class.getName()).log(Level.SEVERE, null, e);
            return "FAIL";
        } finally {
            try {if (con!=null) con.close();} catch (SQLException e) {}
        }
    }
}
