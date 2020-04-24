package lesson36;

import org.junit.*;

public class App36Test {

    public static App36 myClass;

    @BeforeClass
    public static void init() {
        System.out.println("init");
        myClass = new App36();
    }

    @Test(expected = RuntimeException.class)
    //@org.junit.Ignore("пропустить тест 1")
    public void method2test1() {
        int[] array = new int[0];
        int[] resultArray = myClass.method2(array);
    }

    @Test
    public void method2test2() {
        int[] array = {1, 2, 4, 4, 2, 3, 4, 1, 7};
        int[] resultArray = myClass.method2(array);
        Assert.assertArrayEquals(new int[]{1, 7}, resultArray);
    }

    @Test(expected = RuntimeException.class)
    public void method2test3() {
        int[] array = {1, 2, 0, 0, 2, 3, 0, 1, 7};
        int[] resultArray = myClass.method2(array);
    }

    @Test
    public void method2test4() {
        int[] array = {4,4,4,4,4};
        int[] resultArray = myClass.method2(array);
        Assert.assertEquals(0, resultArray.length);
    }

    @Test
    public void method3test1() {
        int[] array = {4,4,4,4,4};
        Assert.assertTrue(myClass.method3(array));
    }

    @Test
    public void method3test2() {
        int[] array = {4,1,4,1,4};
        Assert.assertTrue(myClass.method3(array));
    }

    @Test
    public void method3test3() {
        int[] array = new int[0];
        Assert.assertFalse(myClass.method3(array));
    }

    @Test
    public void method3test4() {
        int[] array = {3,5};
        Assert.assertFalse(myClass.method3(array));
    }
}