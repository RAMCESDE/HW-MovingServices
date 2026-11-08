package app.service.helpers;

import java.util.Locale;
import java.util.UUID;

public final class IdGenerator {

    private IdGenerator() {
    }

    public static String nuevo(String prefijo) {
        return prefijo + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }
}
