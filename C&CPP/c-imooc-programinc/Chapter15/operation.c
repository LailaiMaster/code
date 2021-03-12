#include "operation.h"

double OperationApply(Operation *operation) {
  if (operation->operator) {
    return operation->operator(operation->left, operation->right);
  } else {
    return 0.0;
  }
}

double Plus(double left, double right) {
  return left + right;
}

double Minus(double left, double right) {
  return left - right;
}

double Multiply(double left, double right) {
  return left * right;
}

double Divide(double left, double right) {
    /*用户输入的 0 肯定是 0，不用做精度检测，如果是计算中得到的一个数，就不能这样判断。*/
  if (right == 0.0) {
    return 0;
  } else {
    return left / right;
  }
}

