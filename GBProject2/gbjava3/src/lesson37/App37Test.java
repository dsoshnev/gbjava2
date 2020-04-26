package lesson37;

public class App37Test {

    @lesson37.BeforeSuite
    public void before() {
        System.out.println("before");
    }

    @lesson37.AfterSuite
    public void after() {
        System.out.println("after");
    }

    @lesson37.Test(priority = 1, expected = RuntimeException.class)
    public void test1() {
        System.out.println("test1");
    }

    @lesson37.Test
    public void test2() {
        System.out.println("test2");
    }

    @lesson37.Test(priority = 3, expected = RuntimeException.class)
    public void test3() {
        System.out.println("test3");
    }

    @lesson37.Test(priority = 4)
    public void test4() {
        System.out.println("test4");
    }

}
