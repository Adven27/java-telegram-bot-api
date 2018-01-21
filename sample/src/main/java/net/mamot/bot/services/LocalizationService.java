package net.mamot.bot.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LocalizationService {
    private ResourceBundle english;
    private ResourceBundle russian;

    private static class CustomClassLoader extends ClassLoader {
        public CustomClassLoader(ClassLoader parent) {
            super(parent);

        }

        public InputStream getResourceAsStream(String name) {
            InputStream utf8in = getParent().getResourceAsStream(name);
            if (utf8in != null) {
                try {
                    byte[] utf8Bytes = new byte[utf8in.available()];
                    utf8in.read(utf8Bytes, 0, utf8Bytes.length);
                    byte[] iso8859Bytes = new String(utf8Bytes, "UTF-8").getBytes("ISO-8859-1");
                    return new ByteArrayInputStream(iso8859Bytes);
                } catch (IOException e) {
                    e.printStackTrace();

                } finally {
                    try {
                        utf8in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

    public LocalizationService() {
        CustomClassLoader loader = new CustomClassLoader(Thread.currentThread().getContextClassLoader());
        english = ResourceBundle.getBundle("localisation.strings", new Locale("en", "US"), loader);
        russian = ResourceBundle.getBundle("localisation.strings", new Locale("ru", "RU"), loader);
    }

    /**
     * Get a string in default language (en)
     *
     * @param key key of the resource to fetch
     * @return fetched string or error message otherwise
     */
    public String getString(String key) {
        return getString(key, "en");
    }

    /**
     * Get a string in default language
     *
     * @param key key of the resource to fetch
     * @return fetched string or error message otherwise
     */
    public String getString(String key, String language) {
        try {
            switch (language.toLowerCase()) {
                case "en": return english.getString(key);
                case "ru": return russian.getString(key);
                default: return english.getString(key);
            }
        } catch (MissingResourceException e) {
            return "String not found";
        }
    }
}