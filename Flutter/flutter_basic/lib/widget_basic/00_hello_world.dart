import 'package:flutter/material.dart';

//https://stackoverflow.com/questions/51061147/what-is-the-difference-between-material-and-materialapp-in-flutter
Widget buildHelloWorldApp() =>
//    Material(
//      child: Center(
//        child: new Text(
//          "Hello Flutter",
//          textDirection: TextDirection.ltr,
//        ),
//      ),
//    );

Center(
  child: new Text(
    "The yellow underlines you can find in Text is introduced by MaterialApp as a fallback Theme. It is here for debug purpose, to warn you that you need to use Material somewhere above your Text.",
    textDirection: TextDirection.ltr,
    style: TextStyle(fontSize: 14),
  ),
);