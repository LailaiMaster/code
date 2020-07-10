import 'package:flutter/material.dart';
import 'package:flutter_basic/lifecycle/LifecycleSecondPage.dart';

Widget buildLifecyclePageWidget() {
  return MaterialApp(
    title: "Flutter Lifecycle Demo",
    theme: ThemeData(primaryColor: Colors.blue),
    home: LifecyclePage(),
  );
}

class LifecyclePage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _LifecyclePageState();
  }
}

class _LifecyclePageState extends State<LifecyclePage>
    with WidgetsBindingObserver {
  //构造方法
  _LifecyclePageState() {
    //此时获取 widget 返回 null。
    print("page1 _LifecyclePageState init, widget = $widget");
  }

  //绘制界面
  @override
  Widget build(BuildContext context) {
    print("page1 build......");

    return Scaffold(
      appBar: AppBar(
        title: Text("Lifecycle Demo"),
      ),
      body: Center(
        child: Column(
          children: <Widget>[
            RaisedButton(
              child: Text("打开/关闭新页面查看状态"),
              onPressed: () => Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder: (context) => LifecycleSecondPage())),
            )
          ],
        ),
      ),
    );
  }

  //当Widget第一次插入到Widget树时会被调用。对于每一个State对象，Flutter只会调用该回调一次
  @override
  void initState() {
    super.initState();
    print("page1 initState......");
    WidgetsBinding.instance.addObserver(this); //注册监听器

    WidgetsBinding.instance.addPostFrameCallback((_) {
      print("单次Frame绘制回调"); //只回调一次
    });

    WidgetsBinding.instance.addPersistentFrameCallback((_) {
      print("实时Frame绘制回调"); //每帧都回调
    });
  }

  /*
  *1. 初始化时，在initState之后立刻调用
  *2. 当State的依赖关系发生变化时，会触发此接口被调用
  */
  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
  }

  //当State对象从树中被移除时，会调用此回调
  @override
  void deactivate() {
    super.deactivate();
    print('page1 deactivate......');
  }

  //当State对象从树中被永久移除时调用；通常在此回调中释放资源
  @override
  void dispose() {
    super.dispose();
    print('page1 dispose......');
    WidgetsBinding.instance.removeObserver(this); //移除监听器
  }

  @override
  void setState(VoidCallback fn) {
    super.setState(fn);
    print("page1 setState......");
  }

  //状态改变的时候会调用该方法,比如父类调用了setState
  @override
  void didUpdateWidget(LifecyclePage oldWidget) {
    super.didUpdateWidget(oldWidget);
    print("page1 didUpdateWidget......");
  }

  //监听App生命周期回调
  @override
  void didChangeAppLifecycleState(AppLifecycleState state) async {
    print("$state");
    if (state == AppLifecycleState.resumed) {
      //do sth
    }
  } //end

}
