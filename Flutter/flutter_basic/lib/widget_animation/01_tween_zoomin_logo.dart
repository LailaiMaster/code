import 'package:flutter/animation.dart';
import 'package:flutter/material.dart';

Widget buildTweenAnimationWidget() {
  return Scaffold(
    body: TweenAnimationWidget(),
  );
}

class TweenAnimationWidget extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _TweenAnimationWidgetState();
  }
}

class _TweenAnimationWidgetState extends State<TweenAnimationWidget>
    with SingleTickerProviderStateMixin {

  Animation<double> animation;
  AnimationController animationController;

  @override
  void initState() {
    super.initState();
    //控制器
    animationController = AnimationController(duration: const Duration(milliseconds: 2000), vsync: this);
    //动画
    animation = Tween(begin: 0.0, end: 300.0).animate(animationController)
      ..addListener(() {
        setState(() {});
      });
    //开始动画
    animationController.forward();
  }

  @override
  void dispose() {
    animationController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Container(
        margin: EdgeInsets.symmetric(vertical: 10),
        height: animation.value,
        width: animation.value,
        child: new FlutterLogo(),
      ),
    );
  }
}
