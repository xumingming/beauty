package org.abei.beauty;

import java.util.function.Function;

/**
 * Represents the information of a column in a table, e.g. name, valueExtractor etc.
 *
 * @param <T> the Class of the data to display in the table.
 */
public class Column<T>
{
    public static final Column SEPARATOR = new Column("__separator__", null, null);
    /**
     * Name of the column.
     */
    private final String name;
    /**
     * Function to extract the value of this column from current value object.
     */
    private final Function<T, Object> extractor;
    /**
     * Function to compute the color for this column.
     */
    private final Function<T, Color> colorProvider;

    public Column(String name, Function<T, Object> extractor, Function<T, Color> colorProvider)
    {
        this.name = name;
        this.extractor = extractor;
        this.colorProvider = colorProvider;
    }

    public static <T> Column column(String name, Function<T, Object> extractor)
    {
        return new Column<T>(name, extractor, x -> Color.NONE);
    }

    public static <T> Column column(String name, Function<T, Object> extractor, Function<T, Color> colorProvider)
    {
        return new Column<T>(name, extractor, colorProvider);
    }

    public String getName()
    {
        return name;
    }

    public Function<T, Object> getExtractor()
    {
        return extractor;
    }

    public Color getColor(T current)
    {
        return colorProvider.apply(current);
    }
}
