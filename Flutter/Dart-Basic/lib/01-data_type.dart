import 'dart:ffi';
import 'dart:math';

void main() {
  _sample1_num();
}

//数据类型
//    在 Dart 中，我们可以用 var 或者具体的类型来声明一个变量。当使用 var 定义变量时，表示类型是交由编译器推断决定的，当然你也可以用静态类型去定义变量，更清楚地跟编译器表达你的意图，这样编辑器和编译器就能使用这些静态类型，向你提供代码补全或编译警告的提示了。
//    在默认情况下，未初始化的变量的值都是 null，因此我们不用担心无法判定一个传递过来的、未定义变量到底是 undefined。
//    Dart 是类型安全的语言，并且所有类型都是对象类型，都继承自顶层类型 Object，因此一切变量的值都是类的实例（即对象），甚至数字、布尔值、函数和 null 也都是继承自 Object 的对象。
//    Dart 语言支持以下内建类型：如 num、bool、String、List (也被称为 Array) 和 Map、Set、Rune (用于在字符串中表示 Unicode 字符)、Symbol。
void dataTypeSample() {
  _sample1_num();
  _sample2_bool();
  _sample3_string();
  _sample4_list();
  _sample5_const();
  _sample6_question();
}

//对于集合类型 List 和 Map，如何让其内部元素支持多种类型（比如，int、double）呢？又如何在遍历集合时，判断究竟是何种类型呢？
void _sample6_question() {
//   方式1：使用 dynamic，使用形如 List<dynamic> 和 Map<String, dynamic> 为集合添加不同类型的元素，遍历时判断类型用 is 关键字。但 如果类型是可枚举的，这样做是可以的。但不建议定义容器类型时用 dynamic。
//   方式2：Dart 是支持泛型的，最好还是使用明确类型，比如放double和int的可以用num
  var listNum1 = List<num>.of([1, 2, 3, 4, 5]);
  var listNum2 = List<dynamic>.of([1, 2, 3, 4, 5, 9.9]);

  for (var value in listNum2) {
    if (value.runtimeType == int) {
      //用 runtimeType 判断 判断
      print('value int = $value');
    } else if (value is double) {
      //如果类型是可枚举的，用"if(v is num)" 或 "if(v is String)"也可以
      print('value float = $value');
    }
  }
}

//常量定义需要在定义变量前加上 final 或 const 关键字：
//    const，表示变量在编译期间即能确定的值， const 适用于定义编译常量（字面量固定值）的场景。
//    final 则不太一样，用它定义的变量可以在运行时确定值，而一旦确定后就不可再变，而 final 适用于定义运行时常量的场景。
void _sample5_const() {
  final name = 'Andy';
  const count = 3;

  var x = 70;
  var y = 30;
  final z = x / y;
}

//在 Dart 中的对应实现是 List 和 Map，统称为集合类型，在 Dart 中的 Array 就是 List 对象。
void _sample4_list() {
  //声明list，Dart 会自动根据上下文进行类型推断
  var arr1 = ["Tom", "Andy", "Jack"];
  var arr2 = List.of([1, 2, 3]);
  arr2.add(499);
  arr2.forEach((v) => print('${v}'));

  //也可以显示声明泛型类型
  var arr3 = ['Tom', 'Andy', 'Jack'];
  var arr4 = new List.of([1, 2, 3]);
  arr4.add(499);
  arr4.forEach((v) => print('${v}'));
  print(arr4 is List); // true
}

//在 Dart 中的对应实现是 List 和 Map，统称为集合类型
void sample4_map() {
  //声明map，Dart 会自动根据上下文进行类型推断
  var map1 = {"name": "Tom", 'sex': 'male'};
  var map2 = new Map();
  map2['name'] = 'Tom';
  map2['sex'] = 'male';
  map2.forEach((k, v) => print('${k}: ${v}'));

  //也可以显示声明泛型类型
  var map3 = {
    'name': 'Tom',
    'sex': 'male',
  };
  var map4 = new Map();
  map4['name'] = 'Tom';
  map4['sex'] = 'male';
  map4.forEach((k, v) => print('${k}: ${v}'));
  print(map4 is Map); // true
}

//Dart 的 String 由 UTF-16 的字符串组成。和 JavaScript 一样，构造字符串字面量时既能使用单引号也能使用双引号，还能在字符串中嵌入变量或表达式。
//    为了获得内嵌对象的字符串，Dart 会调用对象的 toString() 方法（与 java 一致）。
//    对于多行字符串的构建，你可以通过三个单引号或三个双引号的方式声明，这与 Python 是一致的。
//    == 运算符用来测试两个对象是否相等。 在字符串中，如果两个字符串包含了相同的编码序列，那么这两个字符串相等。
void _sample3_string() {
  var s = 'cat';
  var s1 = 'this is a uppercased string: ${s.toUpperCase()}';
}

//为了表示布尔值，Dart 使用了一种名为 bool 的类型。在 Dart 里，只有两个对象具有 bool 类型：true 和 false，它们都是编译时常量。
void _sample2_bool() {
  var isSuccess = true;
  print('isSuccess = $isSuccess');
}

// Dart 的数值类型 num，只有两种子类：即 64 位 int 和符合 IEEE 754 标准的 64 位 double。前者代表整数类型，而后者则是浮点数的抽象。
void _sample1_num() {
  int x = 1;
  int hex = 0xEEADBEEF;
  double y = 1.1;
  double exponents = 1.13e5;
  int roundY = y.round();
  print('x = $x');
}
