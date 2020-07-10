//函数：
//    在 Dart 中，所有类型都是对象类型，函数也是对象，它的类型叫作 Function。这意味着函数也可以被定义为变量，甚至可以被定义为参数传递给另一个函数。
//    C++ 与 Java 的做法是，提供函数的重载，即提供同名但参数不同的函数。但Dart 认为重载会导致混乱，因此从设计之初就不支持重载，而是提供了可选命名参数和可选参数。
void functionSample() {
  _sample1_defineFunc();
  _sample2_theParams();
}

void _sample2_theParams() {
  namedParams();
  optionalParams();
}

void _sample1_defineFunc() {
  print('isZeorShort(1) = ${isZeroShort(1)}');
  print('isZeor(1) = ${isZero(1)}');
}

//如果函数体只有一行表达式，就比如上面示例中的 isZero 和 printInfo 函数，可以像 JavaScript 语言那样用箭头函数来简化这个函数
bool isZeroShort(num number) => number == 0;

bool isZero(num number) {
  return number == 0;
}

//给参数增加{}，以 paramName: value 的方式指定调用参数，也就是可选命名参数
void namedParams() {
  enable1Flags(bold: false, hidden: true);
  enable2Flags();
}

// 要达到可选命名参数的用法，那就在定义函数的时候给参数加上 {}
void enable1Flags({bool bold, bool hidden}) => print("$bold , $hidden");
// 定义可选命名参数时增加默认值
void enable2Flags({bool bold = true, bool hidden = false}) =>
    print("$bold ,$hidden");

//给参数增加 []，则意味着这些参数是可以忽略的，也就是可选参数。
void optionalParams() {
  enable3Flags(true);
  enable4Flags(false);
}

// 可忽略的参数在函数定义时用 [] 符号指定
void enable3Flags(bool bold, [bool hidden]) => print("$bold ,$hidden");
// 定义可忽略参数时增加默认值
void enable4Flags(bool bold, [bool hidden = false]) => print("$bold ,$hidden");
