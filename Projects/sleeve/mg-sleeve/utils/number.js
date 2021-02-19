/*解决 JS 浮点数运算不精确的问题*/

/**解决 JS 浮点数运算不精确的问题*/
function accAdd(num1, num2) {
    const num1Digits = (num1.toString().split(".")[1] || "").length;
    const num2Digits = (num2.toString().split(".")[1] || "").length;
    const baseNum = Math.pow(10, Math.max(num1Digits, num2Digits));
    return (Math.round(num1 * baseNum) + Math.round(num2 * baseNum)) / baseNum;
}

/**解决 JS 浮点数运算不精确的问题*/
function accMultiply(num1, num2) {
    const num1Digits = (num1.toString().split(".")[1] || "").length;
    const num2Digits = (num2.toString().split(".")[1] || "").length;
    const baseNum = Math.pow(10, Math.max(num1Digits, num2Digits));
    return (
        (Math.round(num1 * baseNum) * Math.round(num2 * baseNum)) /
        baseNum /
        baseNum
    );
}

/**解决 JS 浮点数运算不精确的问题*/
function accSubtract(num1, num2) {
    const num1Digits = (num1.toString().split(".")[1] || "").length;
    const num2Digits = (num2.toString().split(".")[1] || "").length;
    const baseNum = Math.pow(10, Math.max(num1Digits, num2Digits));
    return (Math.round(num1 * baseNum) - Math.round(num2 * baseNum)) / baseNum;
}

export { accAdd, accMultiply, accSubtract };
