import 'package:flutter/material.dart';
import 'package:flutter_basic/common.dart';

//手势
import 'package:flutter_basic/widget_gesture/02_gesture_detector.dart';
import 'package:flutter_basic/widget_gesture/07_inkwell.dart';
import 'package:flutter_basic/widget_gesture/05_dismissible-item.dart';
import 'package:flutter_basic/widget_gesture/04_draw-signature.dart';
import 'package:flutter_basic/widget_gesture/01_listener.dart';
import 'package:flutter_basic/widget_gesture/06-dragable.dart';
import 'package:flutter_basic/widget_gesture/03_raw_gesture_detector.dart';


List<Page> _buildRoutes() {
  return [
    //basic gesture
    Page("原始事件处理", (context) => buildTouchEventListenerWidget()),
    Page("GestureDetector", (context) => buildGestureDetectorWidget()),
    Page("RawGestureDetector", (context) => buildDoubleGestureWidget()),
    Page("InkWell", (context) => buildInkWellWidget()),
    Page("Dismissible", (context) => buildDismissibleWidget()),
    Page("Drawable", (context) => buildDrawableWidget()),
    Page("Dragable", (context) => buildDragableWidget()),
  ];
}

Widget buildGesturePagesWidget(BuildContext context) =>
    buildListBody("手势", context, _buildRoutes());