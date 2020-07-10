import 'package:flutter/material.dart';
import 'package:flutter_basic/common.dart';

//动画
import 'package:flutter_basic/storage/01_store_by_files.dart';
import 'package:flutter_basic/storage/02_storage_by_sp.dart';
import 'package:flutter_basic/storage/03_storage_by_sql.dart';

List<Page> _buildRoutes() {
  return [
    //Animation
    Page("Store by File", (context) => buildStoreByFilesDemo()),
    Page("Store by Sp", (context) => buildStoreBySpDemo()),
    Page("Store by DB", (context) => buildStoreByDBDemo()),
  ];
}

Widget buildStoragePagesWidget(BuildContext context) =>
    buildListBody("存储示例", context, _buildRoutes());