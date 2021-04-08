package leetcode;

import java.util.Arrays;

public class Q334_ReverseString {

    public static void main(String[] args) {
        //char[] s = {'a', 'b', 'c', 'd'};
        char[] s = {'a', 'b', 'c', 'd', 'e'};
        new Q334_ReverseString().reverseString(s);
        System.out.println(Arrays.toString(s));
    }

    public void reverseString(char[] s) {
        int length = s.length;
        if (length <= 1) {
            return;
        }

        int start = 0;
        int tail = length - 1;
        char temp;

        while (start < tail) {
            temp = s[start];
            s[start] = s[tail];
            s[tail] = temp;

            start++;
            tail--;
        }
    }

}
