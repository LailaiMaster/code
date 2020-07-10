import 'package:flutter/material.dart';

Widget buildInheritedWidget() =>
    Scaffold(
      appBar: AppBar(
        title: Text("InheritedWidget"),
      ),
      body: InheritedWidgetTestRoute(),
    );

class InheritedWidgetTestRoute extends StatefulWidget {

  @override
  _InheritedWidgetTestRouteState createState() =>
      new _InheritedWidgetTestRouteState();

}

class _InheritedWidgetTestRouteState extends State<InheritedWidgetTestRoute> {

  int count = 0;

  @override
  Widget build(BuildContext context) {
    return Center(

      child: ShareDataWidget( //使用ShareDataWidget
        data: count,
        child: _LogColumn(
          children: <Widget>[

            Padding(
              padding: const EdgeInsets.only(bottom: 20.0),
              child: _TestWidget(), //子widget中依赖ShareDataWidget
            ),

            _LogRaisedButton(
              //每点击一次，将count自增，然后重新build,ShareDataWidget的data将被更新
                onPressed: () => setState(() => ++count)
            )

          ],
        ),

      ),
    );
  }

}

/*
InheritedWidget是Flutter中非常重要的一个功能型组件，它提供了一种数据在widget树中从上到下传递、共享的方式，
比如我们在应用的根widget中通过InheritedWidget共享了一个数据，那么我们便可以在任意子widget中来获取该共享的数据！

如Flutter SDK中正是通过InheritedWidget来共享应用主题（Theme）和Locale (当前语言环境)信息的。
 */
class ShareDataWidget extends InheritedWidget {

  ShareDataWidget({@required this.data, Widget child}) : super(child: child){
    print("ShareDataWidget construct");
  }

  //需要在子树中共享的数据，保存点击次数
  final int data;

  //Flutter 中的范式：定义一个便捷方法，方便子树中的widget获取共享数据
  static ShareDataWidget of(BuildContext context) {
    //由于 inheritFromWidgetOfExactType 和 ancestorInheritedElementForWidgetOfExactType 都被标记为废弃的，
    // 现在使用 dependOnInheritedWidgetOfExactType
    return context.dependOnInheritedWidgetOfExactType<ShareDataWidget>();

    /*
    用inheritFromWidgetOfExactType() 和 ancestorInheritedElementForWidgetOfExactType()的区别就是前者会注册依赖关系，而后者不会：

          在调用inheritFromWidgetOfExactType()时，InheritedWidget和依赖它的子孙组件关系便完成了注册，
          之后当InheritedWidget发生变化时，就会更新依赖它的子孙组件，也就是会调这些子孙组件的 didChangeDependencies() 方法和build()方法。

          而当调用的是 ancestorInheritedElementForWidgetOfExactType()时，由于没有注册依赖关系，
          所以之后当InheritedWidget发生变化时，就不会更新相应的子孙Widget，即不会调用其 didChangeDependencies() 方法。
     */
//    return context.inheritFromWidgetOfExactType(ShareDataWidget);
//    return context.ancestorInheritedElementForWidgetOfExactType(ShareDataWidget).widget;
  }

  //该回调决定当data发生变化时，是否通知子树中依赖data的Widget
  @override
  bool updateShouldNotify(ShareDataWidget old) {
    print("updateShouldNotify");
    //如果返回true，则子树中依赖(build函数中有调用)本widget
    //的子widget的`state.didChangeDependencies`会被调用
    return old.data != data;
  }
}

class _LogColumn extends StatelessWidget {

  final List<Widget> children;

  _LogColumn({ this.children}) {
    print("_LogColumn construct");
  }

  @override
  Widget build(BuildContext context) {
    return Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: children);
  }

}

class _LogRaisedButton extends StatelessWidget {

  final VoidCallback onPressed;

  _LogRaisedButton({@required this.onPressed}) {
    print("_LogRaisedButton construct");
  }

  @override
  Widget build(BuildContext context) {
    return RaisedButton(
        child: Text("Increment"),
        onPressed: onPressed);
  }

}

class _TestWidget extends StatefulWidget {

  _TestWidget() {
    print("_TestWidget constructing");
  }

  @override
  __TestWidgetState createState() => new __TestWidgetState();

}

class __TestWidgetState extends State<_TestWidget> {

  __TestWidgetState() {
    print("__TestWidgetState constructing");
  }

  @override
  Widget build(BuildContext context) {
    print("__TestWidgetState build");
    //使用InheritedWidget中的共享数据
    return Text(ShareDataWidget
        .of(context)
        .data
        .toString());
  }

  /*
  一般来说，子widget很少会重写此方法，因为在依赖改变后framework也都会调用build()方法。
  但是，如果你需要在依赖改变后执行一些昂贵的操作，比如网络请求，这时最好的方式就是在此方法中执行，
  这样可以避免每次build()都执行这些昂贵操作。
   */
  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    //父或祖先widget中的InheritedWidget改变(updateShouldNotify返回true)时会被调用。
    //如果build中没有依赖InheritedWidget，则此回调不会被调用。
    print("__TestWidgetState Dependencies change");
    //问题在于，Flutter framework是怎么知道子widget有没有依赖InheritedWidget的？
  }

}