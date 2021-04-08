package a.java.util;

public class ConcurrentHashMapTest {

    static final int HASH_BITS = 0x7fffffff; // usable bits of normal node hash

    public static void main(String[] args) {
        hashTest();
    }

    private static void hashTest() {
        index(hash("15"), 16);
        index(hash("31"), 16);
        index(hash("47"), 16);
        index(hash("63"), 16);
    }

    static int spread(int h) {
        int rh = (h ^ (h >>> 16)) & HASH_BITS;
        System.out.println("spread     \t\t" + toBinary(rh) + "\t\t" + rh);
        return rh;
    }

    static int noneHighSpread(int h) {
        return (h) & HASH_BITS;
    }

    private static int hash(Object key) {
        if (key == null) {
            return 0;
        }

        int h = key.hashCode();
        int rh = h >>> 16;
        System.out.println("===================== hash(" + key + ") ===========================");
        System.out.println("key                  \t\t" + key);
        System.out.println("h=key.hashCode()     \t\t" + toBinary(h) + "\t\t" + h);
        System.out.println("rh=h>>>16            \t\t" + toBinary(rh) + "\t\t" + rh);
        rh = h ^ rh;
        System.out.println("h ^ rh            \t\t" + toBinary(rh) + "\t\t" + rh);
        rh = rh & HASH_BITS;
        System.out.println("h ^ & HASH_BITS            \t\t" + toBinary(rh) + "\t\t" + rh);

        return rh;
    }

    private static String toBinary(int h) {
        return Integer.toBinaryString(h);
    }

    private static int index(int hash, int n) {
        System.out.println("===================== index(" + hash + ", " + n + ") ===========================");
        System.out.println("hash=h ^ rh          \t\t" + toBinary(hash) + "\t\t" + hash);
        System.out.println("n    =               \t\t" + toBinary(n) + "\t\t" + (n));
        System.out.println("n - 1=               \t\t" + toBinary(n - 1) + "\t\t" + (n - 1));
        int index = (n - 1) & hash;
        System.out.println("index=(n - 1) & hash \t\t" + toBinary(index) + "\t\t" + index);
        System.out.println("index=(hash % " + n + " )  \t\t" + toBinary(index) + "\t\t" + index);
        System.out.println();
        return index;
    }

}
