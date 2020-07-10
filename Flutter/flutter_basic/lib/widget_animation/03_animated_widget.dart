import 'package:flutter/animation.dart';
import 'package:flutter/material.dart';

/*
使用动画时，一般使用 StatefulWidget，这样才能对动画进行控制。
 */
Widget buildAnimatedWidgetDemo() {
  return Scaffold(body: AnimatedWidgetDemo());
}

class AnimatedWidgetDemo extends StatefulWidget {
  _AnimatedWidgetDemoState createState() => new _AnimatedWidgetDemoState();
}

class _AnimatedWidgetDemoState extends State<AnimatedWidgetDemo>
    with SingleTickerProviderStateMixin {

  AnimationController controller;
  Animation<double> animation;

  initState() {
    super.initState();
    controller = new AnimationController(duration: const Duration(milliseconds: 2000), vsync: this);
    animation = new Tween(begin: 0.0, end: 300.0).animate(controller);
    controller.repeat();
  }

  Widget build(BuildContext context) {
    return new AnimatedLogo(animation: animation);
  }

  dispose() {
    controller.dispose();
    super.dispose();
  }

}

class AnimatedLogo extends AnimatedWidget {

  AnimatedLogo({Key key, Animation<double> animation})
      : super(key: key, listenable: animation);

  Widget build(BuildContext context) {
    final Animation<double> animation = listenable;

    return new Center(
      child: new Container(
        margin: new EdgeInsets.symmetric(vertical: 10.0),
        height: animation.value,
        width: animation.value,
        child: new FlutterLogo(),
      ),
    );
  }
}
