package com.unimib.koby;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;

/** Utility class usata SOLO nei test (reflection + json helpers). */
public final class TestUtils {

    private TestUtils() {} // no-instantiation

    /**
     * Legge un file JSON presente nella cartella resources del classpath di test
     * e lo restituisce come JsonObject (Gson).
     */
    public static JsonObject readJson(String filename) {
        InputStream is = TestUtils.class.getClassLoader().getResourceAsStream(filename);
        if (is == null) throw new IllegalArgumentException("File non trovato: " + filename);

        try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            throw new RuntimeException("Errore nel parsing di " + filename, e);
        }
    }

    /**
     * Sovrascrive via reflection un campo static final (es. BuildConfig.OPENAI_URL)
     * – utile nei test per reindirizzare URL o chiavi API.
     */
    public static void setFinalStatic(Field field, Object newValue) {
        try {
            field.setAccessible(true);

            // Rimuove il flag FINAL
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(null, newValue);
        } catch (Exception e) {
            throw new RuntimeException("Impossibile modificare campo " + field.getName(), e);
        }
    }
}
