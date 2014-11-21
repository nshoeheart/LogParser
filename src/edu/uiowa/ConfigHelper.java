package edu.uiowa;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by nschuchert on 11/21/14.
 */
public class ConfigHelper {

    public String getProperty(String propName) throws IOException {
        Properties prop = new Properties();
        String propFileName = "edu/uiowa/config.properties";

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        prop.load(inputStream);
        if (inputStream == null) {
            throw new FileNotFoundException("could not locate config.properties file");
        }

        return prop.getProperty(propName);
    }
}
