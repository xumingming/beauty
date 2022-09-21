package org.abei.beauty;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.abei.beauty.Beauty.barChart;
import static org.abei.beauty.Beauty.detail;
import static org.abei.beauty.Beauty.table;
import static org.abei.beauty.Column.column;
import static org.junit.Assert.assertEquals;

public class BeautyTest
{
    @Test
    public void testTable()
    {
        String str = table(
                persons(),
                Arrays.asList(
                        column("Name", (Person p) -> p.getName()),
                        column("Age", (Person p) -> p.getAge()),
                        column("Gender", (Person p) -> p.isMale() ? "Male" : "Female")),
                x -> Color.NONE,
                Color.NONE);

        System.out.println(str);
        assertEquals(
                "Name    Age  Gender  \n" +
                        "------  ---  ------  \n" +
                        "james   10   Male    \n" +
                        "bond    20   Male    \n" +
                        "john    30   Male    \n" +
                        "jack    40   Male    \n" +
                        "lee     50   Male    \n" +
                        "steven  60   Male    \n" +
                        "lily    70   Female  \n" +
                        "lucy    80   Female  \n" +
                        "rachel  90   Female  \n" +
                        "grace   100  Female  \n",
                str);
    }

    @Test
    public void testDetail()
    {
        String str = detail(
                persons().get(0),
                Arrays.asList(
                        column("Name", (Person p) -> p.getName()),
                        column("Age", (Person p) -> p.getAge()),
                        column("Gender", (Person p) -> p.isMale() ? "Male" : "Female")),
                Color.NONE);

        System.out.println(str);
        assertEquals(
                "Name  : james\n" +
                        "Age   : 10\n" +
                        "Gender: Male\n",
                str);
    }

    @Test
    public void testBarChart()
    {
        String str = barChart(BarChart.intBarChart(Arrays.asList(
                new BarItem<>("Male", persons().stream().mapToInt(x -> x.isMale() ? 1 : 0).sum()),
                new BarItem<>("Female", persons().stream().mapToInt(x -> x.isMale() ? 0 : 1).sum()))));

        System.out.println(str);
        assertEquals(
                "\u001B[37;1mName    Value  Percentage  Bar                                                                                                   \n" +
                        "\u001B[0m\u001B[37;1m------  -----  ----------  ----------------------------------------------------------------------------------------------------  \n" +
                        "\u001B[0m\u001B[35mMale    6      60.00%      ████████████████████████████████████████████████████████████                                          \n" +
                        "\u001B[0m\u001B[32mFemale  4      40.00%      ████████████████████████████████████████                                                              \n" +
                        "\u001B[0m",
                str);
    }

    private List<Person> persons()
    {
        Person person0 = new Person("james", 10, true);
        Person person1 = new Person("bond", 20, true);
        Person person2 = new Person("john", 30, true);
        Person person3 = new Person("jack", 40, true);
        Person person4 = new Person("lee", 50, true);
        Person person5 = new Person("steven", 60, true);

        Person person6 = new Person("lily", 70, false);
        Person person7 = new Person("lucy", 80, false);
        Person person8 = new Person("rachel", 90, false);
        Person person9 = new Person("grace", 100, false);

        return Arrays.asList(
                person0, person1, person2, person3, person4,
                person5, person6, person7, person8, person9);
    }

    private static class Person
    {
        private final String name;
        private final int age;
        private final boolean male;

        public Person(String name, int age, boolean male)
        {
            this.name = name;
            this.age = age;
            this.male = male;
        }

        public String getName()
        {
            return name;
        }

        public int getAge()
        {
            return age;
        }

        public boolean isMale()
        {
            return male;
        }
    }
}
