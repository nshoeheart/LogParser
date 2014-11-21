package edu.uiowa;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try {
            ConfigHelper config = new ConfigHelper();
            final String JDBC_DRIVER = "com.mysql.jdbc.Driver"; // Make sure that the driver is added to the class path
            final String DB_URL = config.getProperty("db_url");
            final String USER = config.getProperty("user");
            final String PASS = config.getProperty("pass");

            Connection conn = null;
            PreparedStatement preparedStatement = null;
            String sql = "INSERT INTO LOG_METRICS (TIME, LOG_TYPE, USER_IP, CONTROLLER, ROUTE, RESPONSE_TIME) VALUES (?,?,?,?,?,?)";

            try {
                Class.forName(JDBC_DRIVER);
                System.out.println("Connecting to database...");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println("Successfully connected to database.\n");
                preparedStatement = conn.prepareStatement(sql);

                JFileChooser chooser = new JFileChooser();
                File file = null;

                System.out.println("Select a log file...");
                int returnValue = chooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    file = chooser.getSelectedFile();
                    System.out.println("Log file loaded.\n");
                }

                if (file != null) {
                    try {
                        Scanner scanner = new Scanner(file);
                        int numRows = 0;
                        int metrics = 0;
                        int entries = 0;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

                        System.out.println("Parsing log entries...");
                        //System.out.print("Entries scanned: 0");
                        long start = System.currentTimeMillis();
                        while (scanner.hasNextLine()) {
                            String[] logParts = scanner.nextLine().split(" ", 8);
                            LOG_TYPE type = null;
                            if (logParts.length >= 3) {
                                try {
                                    type = LOG_TYPE.valueOf(logParts[2]);
                                    entries++;
                                } catch (IllegalArgumentException e) {
                                    // not the start of a log entry, do nothing
                                }
                            }

                            if (type == LOG_TYPE.DIAG) {
                                Date time = dateFormat.parse(logParts[0] + " " + logParts[1]);
                                String userAndIP = logParts[5];
                                String controller = logParts[6];
                                String message = " " + logParts[7];

                                if (message.contains("| metrics |")) {
                                    String[] messageParts = message.split(" \\| ");
                                    if (messageParts.length == 6) {
                                        String route = messageParts[4];
                                        int respTime = Integer.valueOf(messageParts[5].split(" ")[0]);

                                        LogEntry entry = new LogEntry(time, type, userAndIP, controller, route, respTime);
                                        metrics++;

                                        preparedStatement.setTimestamp(1, new Timestamp(entry.time.getTime()));
                                        preparedStatement.setString(2, entry.type.name());
                                        preparedStatement.setString(3, entry.userAndIP);
                                        preparedStatement.setString(4, entry.controller);
                                        preparedStatement.setString(5, entry.route);
                                        preparedStatement.setInt(6, entry.responseTime);

                                        preparedStatement.execute();
                                    }
                                }
                            }
                            numRows++;
//                        if (entries % 10000 == 0) {
//                            System.out.print(entries);
//                        } else if (entries % 2500 == 0) {
//                            System.out.print(".");
//                        }
                        }

                        System.out.println("Done!\n");
                        System.out.println("Total metrics entries parsed: " + metrics);
                        System.out.println("Total log entries: " + entries);
                        System.out.println("Total rows: " + numRows);

                        long time = System.currentTimeMillis() - start;
                        long secs = (time / 1000) % 60;
                        long mins = (time / 1000) / 60;
                        System.out.println("Total time - " + (mins != 0 ? mins : 0) + ":" + (secs < 10 ? "0" + secs : secs));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (ParseException p) {
                        p.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load config file");
        }
    }
}
