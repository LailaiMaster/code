package system.chapter02;

public class ArrayGenerator {

    private ArrayGenerator() {
    }

    public static Integer[] generateOrderedArray(int length) {
        Integer[] integerArr = new Integer[length];
        for (int i = 0; i < length; i++) {
            integerArr[i] = i;
        }
        return integerArr;
    }

}
