import 'package:flutter/material.dart';
import 'package:flutter_basic/common.dart';

//动画
import 'package:flutter_basic/state_managing/01_self_managing_state.dart';
import 'package:flutter_basic/state_managing/02_parent_managing_state.dart';
import 'package:flutter_basic/state_managing/03_mix_managing_state.dart';
import 'package:flutter_basic/state_managing/04_provider.dart';
import 'package:flutter_basic/state_managing/05_provder_customer.dart';

List<Page> _buildRoutes() {
  return [
    //Animation
    Page("Selfe Managing", (context) => buildSelfManagingWidget()),
    Page("Parent Managing", (context) => buildParentManagingWidget()),
    Page("Mix Managing", (context) => buildMixManagingWidget()),
    Page("Provider Demo", (context) => buildProviderDemoWidget()),
    Page("Customer Demo", (context) => buildProviderCustomerDemoWidget()),
  ];
}

Widget buildStateManagingPagesWidget(BuildContext context) =>
    buildListBody("状态管理示例", context, _buildRoutes());
