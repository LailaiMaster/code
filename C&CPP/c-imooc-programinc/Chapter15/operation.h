#ifndef CHAPTER15__OPERATION_H_
#define CHAPTER15__OPERATION_H_

/**标识一个运输过程*/
typedef struct {
    /*左操作数*/
    double left;
    /*右操作数*/
    double right;

    /*运算函数*/
    double (*operator)(double, double);
} Operation;

/**执行 Operation */
double OperationApply(Operation *operation);

/**加法*/
double Plus(double left, double right);

/**减法*/
double Minus(double left, double right);

/**乘法*/
double Multiply(double left, double right);

/**除法*/
double Divide(double left, double right);

#endif //CHAPTER15__OPERATION_H_
