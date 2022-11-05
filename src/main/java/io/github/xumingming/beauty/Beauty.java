package io.github.xumingming.beauty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.xumingming.beauty.Color.colorWith;
import static io.github.xumingming.beauty.Column.column;
import static io.github.xumingming.beauty.Utils.padEnd;
import static io.github.xumingming.beauty.Utils.percentage;
import static java.lang.String.format;

public class Beauty
{
    /**
     * The separator string between columns
     */
    private static final String COLUMN_SEPARATOR = "  ";
    private static final Color[] BAR_CHART_COLORS = new Color[] {
            Color.YELLOW,
            Color.GREEN,
            Color.GRAY,
            Color.PURPLE,
            Color.CYAN
    };

    private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss");
    private static final SimpleDateFormat LONG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Beauty()
    {}

    public static <T> String table(List<T> items, List<Column<T>> columns)
    {
        return table(items, columns, Color.WHITE);
    }

    public static <T> String table(List<T> items, List<Column<T>> columns, Color headColor)
    {
        return table(items, columns, headColor, TableStyle.CLICKHOUSE);
    }

    public static <T> String table(List<T> items, List<Column<T>> columns, Color headColor, TableStyle tableStyle)
    {
        if (tableStyle == TableStyle.SIMPLE) {
            return simpleTable(items, columns, headColor);
        }
        else {
            return clickHouseTable(items, columns, headColor);
        }
    }

    private static <T> String simpleTable(List<T> items,
            List<Column<T>> columns,
            Color headColor)
    {
        List<Integer> maxColumnWidth = calculateColumnWidth(items, columns);

        // draw the headline
        StringBuilder ret = new StringBuilder();

        StringBuilder headline = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            Column<T> column = columns.get(i);
            headline.append(padEnd(column.getName(), maxColumnWidth.get(i), ' ')).append(COLUMN_SEPARATOR);
        }
        headline.append("\n");
        ret.append(colorWith(headline, headColor));

        // draw the boundary line
        StringBuilder boundaryLine = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            boundaryLine.append(padEnd("", maxColumnWidth.get(i), '-')).append(COLUMN_SEPARATOR);
        }
        boundaryLine.append("\n");
        ret.append(colorWith(boundaryLine.toString(), headColor));

        for (T item : items) {
            StringBuilder contentLine = new StringBuilder();
            for (int i = 0; i < columns.size(); i++) {
                Column<T> column = columns.get(i);
                Object content = formatTableCell(item, column);

                Color cellColor = getCellColor(item, column);
                contentLine.append(colorWith(padEnd(content.toString(), maxColumnWidth.get(i), ' '), cellColor))
                        .append(COLUMN_SEPARATOR);
            }
            contentLine.append("\n");

            ret.append(contentLine);
        }

        return ret.toString();
    }

    private static <T> String formatTableCell(T item, Column<T> column)
    {
        Object content = column.getExtractor().apply(item);

        if (content instanceof Date && content != null) {
            content = SHORT_DATE_FORMAT.format(content);
        }

        if (content == null) {
            content = "-";
        }
        return content.toString();
    }

    public static <T> String clickHouseTable(List<T> items, List<Column<T>> columns, Color headColor)
    {
        List<Integer> maxColumnWidth = calculateColumnWidth(items, columns);

        // draw the headline
        StringBuilder ret = new StringBuilder();
        // draw the UPPER boundary line
        ret.append(colorWith(getUpperBoundaryLine(maxColumnWidth), headColor));

        StringBuilder headline = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            Column<T> column = columns.get(i);
            headline.append(colorWith("┃ ", headColor))
                    .append(colorWith(padEnd(column.getName(), maxColumnWidth.get(i), ' '), headColor));
            if (i == columns.size() - 1) {
                headline.append(colorWith(" ┃", headColor));
            }
            else {
                headline.append(" ");
            }
        }
        headline.append("\n");
        ret.append(colorWith(headline, headColor));

        // draw the MIDDLE boundary line
        ret.append(colorWith(getMiddleBoundaryLine(columns, maxColumnWidth), headColor));

        for (T item : items) {
            StringBuilder contentLine = new StringBuilder();
            for (int i = 0; i < columns.size(); i++) {
                Column<T> column = columns.get(i);
                String content = formatTableCell(item, column);

                if (i < columns.size()) {
                    contentLine.append(colorWith("│", headColor));
                }
                Color cellColor = getCellColor(item, column);

                contentLine.append(colorWith(padEnd(" " + content, maxColumnWidth.get(i) + 2, ' '), cellColor));
                if (i == columns.size() - 1) {
                    contentLine.append(colorWith("│", headColor));
                }
            }
            contentLine.append("\n");

            ret.append(contentLine);
        }

        // draw the BOTTOM boundary line
        ret.append(colorWith(getBottomBoundaryLine(columns, maxColumnWidth), headColor));

        return ret.toString();
    }

    private static <T> Color getCellColor(T item, Column<T> column)
    {
        Color cellColor = Color.NONE;
        if (column.getColor(item) != null) {
            cellColor = column.getColor(item);
        }
        return cellColor;
    }

    private static <T> List<Integer> calculateColumnWidth(List<T> items, List<Column<T>> columns)
    {
        // 遍历一遍column名字
        List<Integer> maxColumnWidth = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            maxColumnWidth.add(columns.get(i).getName().length());
        }

        // 遍历一遍数据
        for (T item : items) {
            for (int i = 0; i < columns.size(); i++) {
                Column<T> column = columns.get(i);
                Object content = column.getExtractor().apply(item);

                if (content instanceof Date && content != null) {
                    content = SHORT_DATE_FORMAT.format(content);
                }

                if (content == null) {
                    content = "-";
                }

                maxColumnWidth.set(i, Math.max(maxColumnWidth.get(i), content.toString().length()));
            }
        }
        return maxColumnWidth;
    }

    private static <T> String getBoundaryLine(List<Integer> maxColumnWidth, char leftChar, char middleChar, char rightChar, char horizontalChar)
    {
        StringBuilder boundaryLine = new StringBuilder();
        for (int i = 0; i < maxColumnWidth.size(); i++) {
            if (i == 0) {
                boundaryLine.append(leftChar);
            }
            boundaryLine.append(padEnd("", maxColumnWidth.get(i) + 2, horizontalChar));
            if (i < maxColumnWidth.size() - 1) {
                boundaryLine.append(middleChar);
            }
            else {
                boundaryLine.append(rightChar);
            }
        }
        boundaryLine.append("\n");
        return boundaryLine.toString();
    }

    private static <T> String getUpperBoundaryLine(List<Integer> maxColumnWidth)
    {
        return getBoundaryLine(maxColumnWidth, '┏', '┳', '┓', '━');
    }

    private static <T> String getMiddleBoundaryLine(List<Column<T>> columns, List<Integer> maxColumnWidth)
    {
        return getBoundaryLine(maxColumnWidth, '┡', '╇', '┩', '━');
    }

    private static <T> String getBottomBoundaryLine(List<Column<T>> columns, List<Integer> maxColumnWidth)
    {
        return getBoundaryLine(maxColumnWidth, '└', '┴', '┘', '─');
    }

    public static <T> String detail(T item, List<Column<T>> columns)
    {
        return detail(item, columns, Color.WHITE);
    }

    public static <T> String detail(T item, List<Column<T>> columns, Color nameColor)
    {
        StringBuilder ret = new StringBuilder();

        Optional<Integer> nameWidth = columns.stream()
                .map(x -> x.getName().length())
                .max(Comparator.comparingInt(x -> x.intValue()));

        for (Column<T> column : columns) {
            // a separator line
            if (column == Column.SEPARATOR) {
                ret.append(padEnd("-", nameWidth.get(), '-'));
                ret.append("--");
                ret.append(padEnd("-", nameWidth.get(), '-'));
                ret.append("\n");
                continue;
            }

            // the name
            ret.append(Color.colorWith(padEnd(column.getName(), nameWidth.get(), ' '), nameColor)).append(": ");
            // the value
            ret.append(Color.colorWith(column.getExtractor().apply(item), column.getColor(item))).append("\n");
        }

        return ret.toString();
    }

    public static <T> String barChartAsString(BarChart<T> barChart)
    {
        List<BarItem<T>> barItems = barChart.getItems();
        Function<BarItem<T>, Color> colorProvider = barChart.getColorProvider();

        // compute the total value & percentage
        T totalValue = barItems.stream()
                .map(BarItem::getValue)
                .reduce(barChart.getValueIdentitySupplier().get(), (left, right) -> {
                    return barChart.getValueAccumulator().apply(left, right);
                });

        barItems.forEach(barItem -> barItem.setPercentage(
                barChart.getValueDivider().apply(barItem.getValue(), totalValue)));

        return table(
                barItems
                        .stream()
                        .sorted(Comparator.comparing(BarItem<T>::getPercentage).reversed())
                        .collect(Collectors.toList()),
                Arrays.asList(
                        column("Name", (BarItem<T> barItem) -> barItem.getName(), colorProvider),
                        column("Value", (BarItem<T> barItem) -> barChart.getValueFormatter().apply(barItem.getValue()), colorProvider),
                        column("Percentage", (BarItem<T> barItem) -> percentage(barItem.getPercentage()), colorProvider),
                        column("Bar", (BarItem<T> barItem) -> barItem.getBar(), colorProvider)),
                Color.NONE);
    }

    public static void draw(String str)
    {
        System.out.println(str);
    }

    public static void drawError(String str)
    {
        draw(str, Color.RED);
    }

    public static void draw(String str, Color color)
    {
        System.out.println(colorWith(str, color));
    }

    public static int extractIntFromString(String str)
    {
        return Integer.parseInt(str.replaceAll("[^0-9]", ""));
    }

    public static void drawH1Title(String title)
    {
        draw(colorWith(format("==== %s ====", title), Color.YELLOW));
    }

    public static void drawH2Title(String title)
    {
        draw(colorWith(format("== %s ==", title), Color.YELLOW));
    }

    public static final Color getColorByHash(Object object)
    {
        if (object == null) {
            return Color.GRAY;
        }

        return BAR_CHART_COLORS[Math.abs(object.hashCode() * 13 + 4) % BAR_CHART_COLORS.length];
    }

    static {
        SHORT_DATE_FORMAT.setTimeZone(TimeZone.getDefault());
        LONG_DATE_FORMAT.setTimeZone(TimeZone.getDefault());
    }
}
