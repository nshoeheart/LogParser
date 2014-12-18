package edu.uiowa;

import java.util.Date;

/**
 * Created by nschuchert on 11/10/14.
 */
public class LogEntry {
    public Date time;
    public LOG_TYPE type;
    public String ip;
    public String user;
    public String controller;
    public String route;
    public int responseTime; // in milliseconds

    public LogEntry(Date time, LOG_TYPE type, String ip, String user, String controller, String route, int responseTime) {
        this.time = time;
        this.type = type;
        this.ip = ip;
        this.user = user;
        this.controller = controller;
        this.route = route;
        this.responseTime = responseTime;
    }
}
