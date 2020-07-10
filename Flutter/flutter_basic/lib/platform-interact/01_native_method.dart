import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/foundation.dart';

Widget buildNativeMethodDemoWidget() => DefaultPage();

const _platform = MethodChannel('samples.chenhang/navigation');

class DefaultPage extends StatelessWidget {

  DefaultPage({ Key key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.yellowAccent,
      appBar: AppBar(title: Text("Native Method Demo")),
      body: Center(
          child:
          RaisedButton(child: Text("打开应用商店"), onPressed: () {
            _platform.invokeMethod('openAppStore');
          })),

    );
  }
}