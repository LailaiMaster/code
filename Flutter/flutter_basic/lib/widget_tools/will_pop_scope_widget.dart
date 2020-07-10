import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

Widget buildWillPopScopeWidgetSample() =>
    Scaffold(
      appBar: AppBar(title: Text("WillPopScope"),),
      body: WillPopScopeWidgetSample(),
    );

class WillPopScopeWidgetSample extends StatefulWidget {

  @override
  State<StatefulWidget> createState() {
    return WillPopScopeWidgetSampleState();
  }

}

class WillPopScopeWidgetSampleState extends State<WillPopScopeWidgetSample> {

  DateTime _lastPressedAt; //上次点击时间

  @override
  Widget build(BuildContext context) {
    return WillPopScope(child: Center(
      child: Text("1秒内连续按两次返回键退出"),
    ), onWillPop: () async {
      if (_lastPressedAt == null || DateTime.now()
          .difference(_lastPressedAt) > Duration(seconds: 1)) {
        _lastPressedAt = DateTime.now();
        return false;
      }
      return true;
    });
  }

}