package org.acme.config.config;


import io.quarkus.scheduler.Scheduled;

import javax.enterprise.context.ApplicationScoped;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


@ApplicationScoped
public class DatabaseConfigSourceCache {
    public Map<String, String> cacheProperties;
    public LocalDateTime lastChanges = null;

    public DatabaseConfigSourceCache () throws SQLException {
        if (cacheProperties == null) {
            getAllProperties();
        }
    }


    public Map<String, String> getProperties() throws SQLException {
        if (cacheProperties == null) {
            getAllProperties();
        }
        return cacheProperties;
    }


    public String getValue(String propertyName) throws SQLException {
        if (cacheProperties == null) {
            getAllProperties();
        }

        if (cacheProperties.containsKey(propertyName))
            return cacheProperties.get(propertyName);

        return null;
    }



    public void getAllProperties() throws SQLException {
        Connection conn = buildConnection();
        cacheProperties = new HashMap<>();

        ResultSet conf;
        PreparedStatement stmt;
        try {
            stmt = conn.prepareStatement("SELECT name, value, DATE_FORMAT(configurations.updated_at, '%Y-%m-%eT%H:%i:%s') as updated_at FROM configurations ORDER BY updated_at ASC",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            conf = stmt.executeQuery();

            conf.last();
            if (conf.getRow() > 0) {
                System.out.println("=== ***");
                System.out.println("=== *** Es wurde: " + conf.getRow() + " Properties gefunden.");
                System.out.println("=== ***");
            }
            conf.beforeFirst();

            while (conf.next()) {
                compareLastChanges(conf.getString("updated_at"));
                cacheProperties.put(conf.getString("name"), conf.getString("value"));
            }
        } catch (SQLException throwables) {
            System.out.println("=== *** ERROR *** ===");
            System.out.println("=== *** DatabaseConfigSourceCache.getAllProperties");
            System.out.println("=== *** SQLException throwables");
            System.out.println("=== ***");
            throwables.printStackTrace();
        } finally {
            conn.close();
        }
    }

    /*
        Cron. Überprüft jede 5 min, ob irgendwelche Einstellungen in DB geändert wurden:
        Es wird DateTime Feld "configurations.updated_at" überprüft, ob es später als locale Variable "lastChanges" ist.
    */
    @Scheduled(every="5m")
    void refreshProperties() {
        Connection conn = buildConnection();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("=== ***");
        System.out.println("=== *** ========================= Start Sheduler " + dtf.format(now) + " =========================");
        System.out.println("=== ***");
        System.out.println("=== *** lastChanges: " + lastChanges);
        System.out.println();

        ResultSet conf = null;
        PreparedStatement stmt;
        try {
            stmt = conn.prepareStatement("SELECT name, value, DATE_FORMAT(configurations.updated_at, '%Y-%m-%eT%H:%i:%s') as updated_at FROM configurations where DATE_FORMAT(updated_at, '%Y-%m-%e %H:%i:%s') > DATE_FORMAT(?, '%Y-%m-%e %H:%i:%s')",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            stmt.setString(1, lastChanges.toString());
            conf = stmt.executeQuery();

            conf.last();
            if (conf.getRow() > 0) {
                System.out.println("=== ***");
                System.out.println("=== *** Es wurde: " + conf.getRow() + " Änderungen gefunden.");
                System.out.println("=== ***");
            }
            conf.beforeFirst();

            while (conf.next()) {
                compareLastChanges(conf.getString("updated_at"));
                if (cacheProperties.containsKey(conf.getString("name"))) {
                    cacheProperties.replace(conf.getString("name"), conf.getString("value"));
                } else {
                    cacheProperties.put(conf.getString("name"), conf.getString("value"));
                }
            }
        } catch (SQLException throwables) {
            System.out.println("=== *** ERROR *** ===");
            System.out.println("=== *** DatabaseConfigSourceCache.refreshProperties");
            System.out.println("=== *** SQLException throwables");
            System.out.println("=== ***");
            throwables.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.out.println("=== *** ERROR *** ===");
                System.out.println("=== *** DatabaseConfigSourceCache.refreshProperties");
                System.out.println("=== *** Connection konnte nicht geschlossen werden");
                System.out.println("=== ***");
                ex.printStackTrace();
            }
        }
    }

    /*
        Update lastChanges
     */
    void compareLastChanges(String newValue) {
        if (lastChanges == null || lastChanges.isBefore(LocalDateTime.parse(newValue)))  {
            lastChanges = LocalDateTime.parse(newValue);
            System.out.println("=== *** lastChanges.changeTo: " + lastChanges);
        }
    }

    public Connection buildConnection() {
        Connection conn = null;
        try {
            Properties props = new Properties();
            props.setProperty("user", "user");
            props.setProperty("password", "password");
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

            try {
                System.out.println("=== *** Try Prod connection: " + "jdbc:mysql://" + System.getenv("DATABASE_HOST") + ":" + System.getenv("DATABASE_PORT") + "/database-name");
                conn = DriverManager.getConnection("jdbc:mysql://" + System.getenv("DATABASE_HOST") + ":" + System.getenv("DATABASE_PORT") + "/database-name", props);
                System.out.println("=== *** Prod connection was activated:");
                System.out.println("=== *** jdbc:mysql://" + System.getenv("DATABASE_HOST") + ":" + System.getenv("DATABASE_PORT") + "/database-name");
            } catch (Exception ex) {
                try {
                    System.out.println("=== *** Try Dev connection: jdbc:mysql://0.0.0.0:6603/database-name");
                    conn = DriverManager.getConnection("jdbc:mysql://0.0.0.0:6603/database-name", props);
                    System.out.println("=== *** Dev connection was activated:");
                    System.out.println("=== *** jdbc:mysql://0.0.0.0:6603/database-name");
                } catch (Exception e) {
                    System.out.println("=== *** ERROR *** ===");
                    System.out.println("=== *** DatabaseConfigSourceCache.buildConnection");
                    System.out.println("=== *** No connection to Database available.");
                    System.out.println("=== ***");
                    System.out.println(e);
                }
            }
            return conn;
        } catch (Throwable ex) {
            System.out.println("=== *** ERROR *** ===");
            System.out.println("=== *** DatabaseConfigSourceCache.buildConnection");
            System.err.println("=== *** Initial SessionFactory creation failed.");
            System.out.println("=== ***");
            throw new ExceptionInInitializerError(ex);
        }
    }
}
