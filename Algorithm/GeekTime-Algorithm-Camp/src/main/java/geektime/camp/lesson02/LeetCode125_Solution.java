package geektime.camp.lesson02;

/**
 * 自顶向下的编程方式
 */
class LeetCode125_Solution {

    public boolean isPalindrome(String s) {
        //1 filter out number and char
        String filteredStr = filerNonNumberAndChar(s);
        //2 reverse and compare
        String reversedS = reverseString(filteredStr);
        return filteredStr.compareToIgnoreCase(reversedS) == 0;
    }

    private String reverseString(String filteredStr) {
        return new StringBuilder(filteredStr).reverse().toString();
    }

    private String filerNonNumberAndChar(String str) {
        return str.replaceAll("[^A-Za-z0-9]]", "");
    }

}