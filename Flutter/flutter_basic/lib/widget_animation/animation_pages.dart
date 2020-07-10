import 'package:flutter/material.dart';
import 'package:flutter_basic/common.dart';

//动画
import 'package:flutter_basic/widget_animation/01_tween_zoomin_logo.dart';
import 'package:flutter_basic/widget_animation/02_curve_animation.dart';
import 'package:flutter_basic/widget_animation/03_animated_widget.dart';
import 'package:flutter_basic/widget_animation/04_animation_builder.dart';
import 'package:flutter_basic/widget_animation/05_hero_animation.dart';
import 'package:flutter_basic/generated/i18n.dart';

List<Page> _buildAnimationRoutes() {
  return [
    //Animation
    Page("TweenAnimation", (context) => buildTweenAnimationWidget()),
    Page("CurveAnimate", (context) => buildCurveAnimateWidget()),
    Page("AnimatedWidget", (context) => buildAnimatedWidgetDemo()),
    Page("Animated Builder", (context) => buildAnimationBuilderWidget()),
    Page("HeroAnimation", (context) => buildHeroAnimation()),
  ];
}

Widget buildAnimationPagesWidget(BuildContext context) =>
    buildListBody(S.of(context).animation, context, _buildAnimationRoutes());