import 'package:flutter/material.dart';
import 'package:flutter_basic/common.dart';

//入门
import 'package:flutter_basic/widget_basic/00_hello_world.dart';
import 'package:flutter_basic/widget_basic/01_basic_widget.dart';
import 'package:flutter_basic/widget_basic/02_material_widget.dart';
import 'package:flutter_basic/widget_basic/03_process_gesture.dart';
import 'package:flutter_basic/widget_basic/04_stateful_widget.dart';
import 'package:flutter_basic/widget_basic/05_shopping_cart.dart';
import 'package:flutter_basic/widget_basic/01_display_images.dart';

List<Page> _buildRoutes() {
  return [
    //basic widget
    Page("Text", (context) => buildHelloWorldApp()),
    Page("Basic", (context) => buildBasicWidgetApp()),
    Page("Material", (context) => buildMaterialWidget()),
    Page("TapAbleButton", (context) => buildTapAbleButton()),
    Page("Stateful", (context) => buildStatefulWidget()),
    Page("ShoppingCartList", (context) => buildShoppingCartList()),
    Page("ImageList", (context) => buildImageList()),
  ];
}

Widget buildBasicWidgetPagesWidget(BuildContext context) =>
    buildListBody("基础组件", context, _buildRoutes());