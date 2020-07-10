// 1 类：类是特定类型的数据和方法的集合，也是创建对象的模板。与其他语言一样，Dart 为类概念提供了内置支持。
//    在Dart 中，实例变量与实例方法、类变量与类方法的声明与 Java 类似。
//    Dart 中并没有 public、protected、private 这些关键字，我们只要在声明变量与方法时，在前面加上“_”即可作为 private 方法使用。如果不加“_”，则默认为public。不过，“_”的限制范围并不是类访问级别的，而是库访问级别。
void classSample() {
  _defineClassSample();
  _reuseSample();
}

// 定义类："extends Object"可选的，默认继承自 Object。
class Point extends Object {
  num x, y, z;
  static num factor = 0;

  // "this.x, this.y"：语法糖，等同于在函数体内：this.x = x;this.y = y;
  // "z = 0"：与 C++ 类似，Dart 支持初始化列表
  Point(this.x, this.y) : z = 0;

  //Dart 还提供了命名构造函数的方式，使得类的实例化过程语义更清晰。
  Point.bottom(num x) : this(x, 0); // 重定向构造函数

  void printInfo() => print('($x, $y)');

  static void printZValue() => print('$factor');

  @override
  String toString() {
    return 'Point{x: $x, y: $y, z: $z}';
  }
}

void _defineClassSample() {
  var point1 = Point.bottom(3);
  print(point1);
}

//2 复用
//    在面向对象的编程语言中，将其他类的变量与方法纳入本类中进行复用的方式一般有两种：继承父类和接口实现。
//
//    在 Dart 中，你可以对同一个父类进行继承或接口实现：
//      1. 继承父类意味着，子类由父类派生，会自动获取父类的成员变量和方法实现，子类可以根据需要覆写构造函数及父类方法。
//      2. 接口实现则意味着，子类获取到的仅仅是接口的成员变量符号和方法符号，需要重新实现成员变量，以及方法的声明和初始化，否则编译器会报错。
//
//    除了继承和接口实现之外，Dart 还提供了另一种机制来实现类的复用，即“混入”（Mixin）。混入鼓励代码重用，可以被视为具有实现方法的接口。要使用混入，只需要 with 关键字即可。
//          继承歧义，也叫菱形问题，是支持多继承的编程语言中一个相当棘手的问题。当 B 类和 C 类继承自 A 类，而 D 类继承自 B 类和 C 类时会产生歧义。如果 A 中有一个方法在 B 和 C 中已经覆写，而 D 没有覆写它，那么 D继承的方法的版本是 B 类，还是 C 类的呢？

//Vector 继承自 Point
class Vector extends Point {
  num a = 0;

  Vector(num x, num y) : super(x, y);

  // 覆写了 printInfo 实现
  @override
  void printInfo() => print('($x,$y,$z)');
}

// Coordinate 是对 Point 的接口实现
class Coordinate implements Point {
  // 成员变量需要重新声明
  @override
  num x = 0, y = 0, z = 0;

  // 成员函数需要重新声明实现
  @override
  void printInfo() => print('($x,$y)');
}

class NoConstructPoint {
  num x, y = 0;
}

// Coordinate 是对 Point 的接口实现
class MixCoordinate with NoConstructPoint {}

void _reuseSample() {
  var vector = Vector(1, 3);
  var coordinate = Coordinate();
  //级联运算符，等同于 xxx.x=1; xxx.y=2;xxx.z=3
  coordinate
    ..x = 1
    ..y = 2
    ..z = 3;
  var mixCoordinate = MixCoordinate();
  print('vector.runtimeType = ${vector.runtimeType}');
  print('coordinate.x = ${coordinate.x}');
  print('mixCoordinate.x = $mixCoordinate.x');
}
