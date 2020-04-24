package lesson36;

import java.util.Arrays;

public class App36 {
    // 2. Написать метод, которому в качестве аргумента передается не пустой одномерный целочисленный массив.
    //  Метод должен вернуть новый массив, который получен путем вытаскивания из исходного массива элементов,
    //  идущих после последней четверки. Входной массив должен содержать хотя бы одну четверку, иначе в методе
    //  необходимо выбросить RuntimeException. Написать набор тестов для этого метода
    //  (по 3-4 варианта входных данных). Вх: [ 1 2 4 4 2 3 4 1 7 ] -> вых: [ 1 7 ].

    // 3. Написать метод, который проверяет состав массива из чисел 1 и 4. Если в нем нет хоть одной
    // четверки или единицы, то метод вернет false; Написать набор тестов для этого метода
    // (по 3-4 варианта входных данных).

    public static void main(String[] args) {
        App36 c = new App36();
        System.out.println(Arrays.toString(c.method2(new int[]{1, 2, 4, 4, 2, 3, 4, 1, 7})));
    }

    public int[] method2(int[] array) {
        int i;
        for (i = array.length-1; i >= 0 ; i--) {
            if(array[i] == 4) {
                break;
            }
        }
        if(++i == 0) {
            throw new RuntimeException("4 not found");
        }

        int[] newArray = new int[array.length - i];
        System.arraycopy(array, i, newArray, 0, newArray.length);
        return newArray;
    }

    public boolean method3(int[] array) {
        int count = 0;
        for (int value : array) {
            if (value == 1 || value == 4) {
                count++;
            }
        }
        return count > 0;
    }

}
