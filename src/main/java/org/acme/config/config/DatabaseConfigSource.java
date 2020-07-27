package org.acme.config.config;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.config.spi.ConfigSource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RegisterForReflection
public class DatabaseConfigSource implements ConfigSource {

    DatabaseConfigSourceCache databaseConfigSourceCache;


    public DatabaseConfigSource() {
        if (databaseConfigSourceCache == null) {
            try {
                databaseConfigSourceCache = new DatabaseConfigSourceCache();
            } catch (SQLException throwables) {
                System.out.println("=== *** ERROR *** ===");
                System.out.println("=== *** DatabaseConfigSource.constructor");
                System.out.println("=== *** No DatabaseConfigSourceCache.class available.");
                System.out.println("=== ***");
                throwables.printStackTrace();
            }
        }
    }

    @Override
    public Map<String, String> getProperties() {
        final Map<String, String> properties = new HashMap<>();

        try {
            return databaseConfigSourceCache.getProperties();
        } catch (Exception throwables) {
            System.out.println("=== *** ERROR *** ===");
            System.out.println("=== *** DatabaseConfigSource.getProperties");
            System.out.println("=== *** No databaseConfigSourceCache.getProperties() available.");
            System.out.println("=== ***");
            throwables.printStackTrace();
        }
        return properties;
    }

    @Override
    public String getValue(String propertyName) {
        try {
            if (databaseConfigSourceCache.getValue(propertyName) != null) {
                System.out.println("=== ***");
                System.out.println("=== *** ----------------------- DatabaseConfigSource.getValue ------------------------");
                System.out.println("=== *** ----------------------- " + propertyName + ": " + databaseConfigSourceCache.getValue(propertyName));
                System.out.println("=== ***");
            }

            return databaseConfigSourceCache.getValue(propertyName);
        } catch (Exception throwables) {
            System.out.println("=== *** ERROR *** ===");
            System.out.println("=== *** DatabaseConfigSource.getValue");
            System.out.println("=== *** No databaseConfigSourceCache.getValue(propertyName) available.");
            System.out.println("=== ***");
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public String getName() {
        return DatabaseConfigSource.class.getSimpleName();
    }
}
