package me.progape.java.datachannel;

import com.google.common.io.Resources;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author progape
 * @date 2022-02-13
 */
public class BaseTest {
    protected Properties loadProperties(String resourceName) {
        Properties properties = new Properties();
        try (InputStream is = Files.newInputStream(Paths.get(Resources.getResource(resourceName).getFile()))) {
            properties.load(is);
        } catch (Throwable th) {
            th.printStackTrace();
            throw new RuntimeException(th);
        }
        return properties;
    }
}
