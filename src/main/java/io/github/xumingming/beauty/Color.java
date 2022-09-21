package io.github.xumingming.beauty;

/**
 * Represents ANSI Color Code.
 */
public enum Color
{
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    GRAY("\u001B[37m"),
    WHITE("\u001B[37;1m"),
    RESET("\u001B[0m"),
    NONE(null);
    private String ansiColorCode;

    Color(String ansiColorCode)
    {
        this.ansiColorCode = ansiColorCode;
    }

    public static String colorWith(Object contentObj, Color color)
    {
        String content = "-";
        if (contentObj != null) {
            content = contentObj.toString();
        }

        if (color != Color.NONE) {
            return color.ansiColorCode + content + Color.RESET.ansiColorCode;
        }

        return content;
    }
}
