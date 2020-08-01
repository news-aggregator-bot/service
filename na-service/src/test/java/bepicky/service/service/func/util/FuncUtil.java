package bepicky.service.service.func.util;

public class FuncUtil {

    private FuncUtil() {}

    public static String normalizeName(String name) {
        return name.replaceAll(" ", "-")
            .replaceAll("'", "")
            .replaceAll("\\?", "")
            .replaceAll("!", "");
    }
}
