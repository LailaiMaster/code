import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

Widget buildThemeDemo() =>
    Scaffold(
      appBar: AppBar(title: Text("Color Theme"),),
      body: ThemeDemoWidget(),
    );

class ThemeDemoWidget extends StatefulWidget {

  @override
  State<StatefulWidget> createState() {
    return ThemeDemoWidgetState();
  }

}

class ThemeDemoWidgetState extends State<ThemeDemoWidget> {

  @override
  Widget build(BuildContext context) {
    return Column(
      children: <Widget>[
        Padding(padding: EdgeInsets.symmetric(vertical: 20)),
        //背景为蓝色，则title自动为白色
        NavBar(color: Colors.blue, title: "标题"),
        Padding(padding: EdgeInsets.symmetric(vertical: 20)),
        //背景为白色，则title自动为黑色
        NavBar(color: Colors.white, title: "标题"),
      ],
    );
  }

}

class NavBar extends StatelessWidget {

  final String title;
  final Color color; //背景颜色

  NavBar({
    Key key,
    this.color,
    this.title,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      constraints: BoxConstraints(
        minHeight: 52,
        minWidth: double.infinity,
      ),

      decoration: BoxDecoration(
        color: color,
        boxShadow: [
          //阴影
          BoxShadow(
            color: Colors.black26,
            offset: Offset(0, 3),
            blurRadius: 3,
          ),
        ],
      ),

      child: Text(
        title,
        style: TextStyle(
          fontWeight: FontWeight.bold,
          //根据背景色亮度来确定Title颜色
          color: color.computeLuminance() < 0.5 ? Colors.white : Colors.black,
        ),
      ),

      alignment: Alignment.center,
    );
  }
}