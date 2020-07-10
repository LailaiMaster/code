import 'package:flutter/material.dart';
import 'package:flutter_basic/common.dart';

//布局
import 'package:flutter_basic/widget_layout/01_layout.dart';
import 'package:flutter_basic/widget_layout/02_layout.dart';
import 'package:flutter_basic/widget_layout/03_constrained_box.dart';
import 'package:flutter_basic/widget_layout/04_material_pager1.dart';
import 'package:flutter_basic/widget_layout/04_material_pager2.dart';
import 'package:flutter_basic/widget_layout/04_material_pager3.dart';
import 'package:flutter_basic/widget_layout/06_update-item.dart';

List<Page> _buildLayoutRoutes() {
  return [
    //layout
    Page("LayoutDemo", (context) => buildLayoutWidget()),
    Page("LayoutDemoInteractive", (context) => buildLayoutInteractiveWidget()),
    Page("ConstrainedBox", (context) => buildConstrainedBoxWidget()),
    Page("MaterialPager1", (context) => buildMaterialPagerWidget1()),
    Page("MaterialPager2", (context) => buildMaterialPagerWidget2()),
    Page("MaterialPager3", (context) => buildMaterialPagerWidget3()),
    Page("UpdateItem", (context) => buildUpdateItemWidget())
  ];
}

Widget buildLayoutPagesWidget(BuildContext context) =>
    buildListBody("布局", context, _buildLayoutRoutes());