import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter_basic/widget_tools/widget_tools_pages.dart';
import 'package:provider/provider.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'generated/i18n.dart';

import 'common.dart';

import 'widget_animation/animation_pages.dart';
import 'storage/storage_pages.dart';
import 'platform-interact/platform_interact_pages.dart';
import 'state_managing/state-managing-pages.dart';
import 'state_managing/counter_model.dart';
import 'widget_layout/layout_pages.dart';
import 'widget_list_scroll/list_pages.dart';
import 'widget_gesture/gesture_pages.dart';
import 'passing-value/pass_value_pages.dart';
import 'widget_basic/basic_widget_pages.dart';
import 'widget_custom/draw_pages.dart';
import 'lifecycle/lifecycle_pages.dart';
import 'navigator/navigation_pages.dart';
import 'network/networ_pages.dart';

void main() {
  //重写错误处理
  FlutterError.onError = (FlutterErrorDetails details) async {
    // 转发至 Zone 中
    Zone.current.handleUncaughtError(details.exception, details.stack);
  };

  //自定义错误界面
  /*ErrorWidget.builder = (FlutterErrorDetails flutterErrorDetails){
    print(flutterErrorDetails.toString());
    return Scaffold(
        body: Center(
          child: Text("Custom Error Widget"),
        )
    );
  };*/

  //以沙盒机制运行 app
  runZoned(() async {
    runApp(new FlutterBasicWidget());
  }, onError: (error) async {
    print("error: $error");
  });
}

List<Page> _buildModulePages() {
  return [
    Page("基础组件", (context) => buildBasicWidgetPagesWidget(context)),
    Page("布局组件", (context) => buildLayoutPagesWidget(context)),
    Page("列表组件", (context) => buildListPagesWidget(context)),
    Page("动画组件", (context) => buildAnimationPagesWidget(context)),
    Page("手势组件", (context) => buildGesturePagesWidget(context)),
    Page("绘制组件", (context) => buildCustomViewPagesWidget(context)),
    Page("功能组件", (context) => buildToolsPagesWidget(context)),
    Page("存储", (context) => buildStoragePagesWidget(context)),
    Page("平台交互", (context) => buildPlatformInteractPagesWidget(context)),
    Page("状态管理", (context) => buildStateManagingPagesWidget(context)),
    Page("数据传递", (context) => buildPassValuePagesWidget(context)),
    Page("导航", (context) => buildNavigationPagesWidget(context)),
    Page("网络", (context) => buildNetworkPagesWidget(context)),
    Page("生命周期", (context) => buildLifecyclePagesWidget(context)),
  ];
}

class FlutterBasicWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      //for demonstrate provider
        providers: [
          Provider.value(value: 30.0),
          ChangeNotifierProvider.value(value: CounterModel())
        ],
        child: MaterialApp(

          //国际化翻译代理
          localizationsDelegates: [
            S.delegate,
            // ... app-specific localization delegate[s] here
            GlobalMaterialLocalizations.delegate,
            GlobalWidgetsLocalizations.delegate,
            GlobalCupertinoLocalizations.delegate,
          ],
          //设置支持的语言
          supportedLocales: S.delegate.supportedLocales,
          //动态的设置title，便于国际化
          onGenerateTitle: (context) {
            return S
                .of(context)
                .app_title;
          },

          //性能检测开关
          checkerboardOffscreenLayers: false,
          checkerboardRasterCacheImages: false,

          /*配置App主题*/
          theme: ThemeData(
            //颜色模式，亮色、暗色
              brightness: Brightness.dark
          ),

          //首页
          home: buildListBody("Flutter", context, _buildModulePages()),
        ));
  }
}