import 'package:flutter/material.dart';
import 'package:flutter_basic/common.dart';

//列表
import 'package:flutter_basic/widget_list_scroll/01_simple_listview.dart';
import 'package:flutter_basic/widget_list_scroll/02_horizontal_listview.dart';
import 'package:flutter_basic/widget_list_scroll/03_long_listview.dart';
import 'package:flutter_basic/widget_list_scroll/04_multi_type_listview.dart';
import 'package:flutter_basic/widget_list_scroll/05_grid.dart';
import 'package:flutter_basic/widget_list_scroll/06_load_more_list.dart';
import 'package:flutter_basic/widget_list_scroll/07_sliver_scroll.dart';
import 'package:flutter_basic/widget_list_scroll/08_notification_listener.dart';

List<Page> _buildRoutes() {
  return [
    Page("SimpleList", (context) => buildSimpleList()),
    Page("HorizontalListView", (context) => buildHorizontalListView()),
    Page("LongListView", (context) => buildLongListView()),
    Page("MultiListView", (context) => buildMultiListView()),
    Page("GridView", (context) => buildGridViewWidget()),
    Page("InfiniteListView", (context) => buildInfiniteListView()),
    Page("CustomScrollView", (context) => buildCustomScrollView()),
    Page("ScrollNotification", (context) => buildScrollNotificationWidget()),
  ];
}

Widget buildListPagesWidget(BuildContext context) =>
    buildListBody("列表", context, _buildRoutes());