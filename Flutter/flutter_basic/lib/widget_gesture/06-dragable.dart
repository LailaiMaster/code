import 'package:flutter/material.dart';

Widget buildDragableWidget() => DragableWidget();

class DragableWidget extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => DragableWidgetState();
}

class DragableWidgetState extends State<DragableWidget> {
  double _top = 0.0; //距顶部的偏移
  double _left = 0.0; //距左边的偏移

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("DragableWidget")),
      body: Stack(
        children: <Widget>[
          Positioned(
            left: _left,
            top: _top,
            child: GestureDetector(
              child: Container(color: Colors.red, width: 50, height: 50),
              onTap: () => print("Tap"),
              onLongPress: () => print("Long Press"),
              onPanUpdate: (e) {
                setState(() {
                  _top += e.delta.dy;
                  _left += e.delta.dx;
                });
              },
            ),
          )
        ],
      ),
    );
  }
}
