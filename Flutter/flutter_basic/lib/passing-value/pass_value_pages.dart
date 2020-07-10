import 'package:flutter/material.dart';
import 'package:flutter_basic/common.dart';


//传递数据
import 'package:flutter_basic/passing-value/01-passing-value-by-inherrited.dart';
import 'package:flutter_basic/passing-value/02-passing-value-by-notification.dart';
import 'package:flutter_basic/passing-value/03-passing-value-by-eventbus.dart';

List<Page> _buildRoutes() {
  return [
    //passing value
    Page("PassValue-Inherited", (context) => buildPassValueByInheritedWidget()),
    Page("PassValue-Notification", (context) => buildPassValueByNotification()),
    Page("PassValue-EventBus", (context) => buildPassValueByEventBus()),
  ];
}

Widget buildPassValuePagesWidget(BuildContext context) =>
    buildListBody("传递数据", context, _buildRoutes());