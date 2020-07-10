import 'package:flutter/material.dart';
import 'package:flutter_basic/common.dart';

//动画
import 'package:flutter_basic/platform-interact/01_native_method.dart';
import 'package:flutter_basic/platform-interact/02_native_view.dart';

List<Page> _buildRoutes() {
  return [
    //Animation
    Page("Native Method", (context) => buildNativeMethodDemoWidget()),
    Page("Native View", (context) => buildNativeViewDemoWidget()),
  ];
}

Widget buildPlatformInteractPagesWidget(BuildContext context) =>
    buildListBody("平台交互示例", context, _buildRoutes());