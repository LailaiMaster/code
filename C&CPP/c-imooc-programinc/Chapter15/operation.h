#ifndef CHAPTER15__OPERATION_H_
#define CHAPTER15__OPERATION_H_

typedef struct {
  double left;
  double right;
  double (*operator)(double, double);
} Operation;

double OperationApply(Operation *operation);

double Plus(double left, double right);

double Minus(double left, double right);

double Multiply(double left, double right);

double Divide(double left, double right);

#endif //CHAPTER15__OPERATION_H_
