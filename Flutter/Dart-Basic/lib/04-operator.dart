//运算符：Dart 和绝大部分编程语言的运算符一样，所以你可以用熟悉的方式去执行程序代码运算。
void operatorSample() {
  _specialOperatorSample();
}

//特殊的运算符：出来常规的运算符外，Dart 多了几个额外的运算符，用于简化处理变量实例缺失（即 null）的情况。
//    "?."运算符：假设 Point 类有 printInfo() 方法，p 是 Point 的一个可能为 null 的实例。那么，p 调用成员方法的安全代码，可以简化为 p?.printInfo() ，表示 p 为 null 的时候跳过，避免抛出异常。（类似于 kotlin）
//    "??=" 运算符：如果 a 为 null，则给 a 赋值 value，否则跳过。这种用默认值兜底的赋值语句在 Dart 中我们可以用 a ??= value 表示。
//    "??"运算符：如果 a 不为 null，返回 a 的值，否则返回 b。在 Java 或者 C++ 中，我们需要通过三元表达式 (a != null)? a : b 来实现这种情况。而在 Dart 中，这类代码可以简化为 a ?? b。
void _specialOperatorSample() {
  var i;
  print('i.toString()) = ${i.toString()}');
  print('i?.round() = ${i?.round()}');
  var b = i ?? 4;
  print('b.toString()) = ${b.toString()}');
  i ??= 3;
  print('i.round() = ${i.round()}');
}

//运算符也是对象：在 Dart 中，一切都是对象，就连运算符也是对象成员函数的一部分。
//    对于系统的运算符，一般情况下只支持基本数据类型和标准库中提供的类型。而对于用户自定义的类，如果想支持基本操作，比如比较大小、相加相减等，则需要用户自己来定义关于这个运算符的具体实现。
//    Dart 提供了类似 C++ 的运算符覆写机制，使得我们不仅可以覆写方法，还可以覆写或者自定义运算符。
class CustomOp {
  num x, y;

  CustomOp(this.x, this.y);

  // 自定义相加运算符，实现向量相加
  // operator 是 Dart 的关键字，与运算符一起使用，表示一个类成员运算符函数。
  CustomOp operator +(CustomOp v) => CustomOp(x + v.x, y + v.y);

  // 覆写相等运算符，判断向量相等
  @override
  bool operator ==(dynamic v) => x == v.x && y == v.y;
}
