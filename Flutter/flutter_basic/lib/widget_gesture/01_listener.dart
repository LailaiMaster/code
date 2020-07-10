import 'package:flutter/material.dart';
import '../common.dart';

/*
原始事件处理：

当指针按下时，Flutter会对应用程序执行命中测试(Hit Test)，以确定指针与屏幕接触的位置存在哪些组件（widget），
指针按下事件（以及该指针的后续事件）然后被分发到由命中测试发现的最内部的组件，然后从那里开始，事件会在组件树中向上冒泡，
这些事件会从最内部的组件被分发到组件树根的路径上的所有组件，这和Web开发中浏览器的事件冒泡机制相似，
但是Flutter中没有机制取消或停止“冒泡”过程，而浏览器的冒泡是可以停止的。注意，只有通过命中测试的组件才能触发事件。
 */

Widget buildTouchEventListenerWidget() => SampleWidget(_choices, "原始事件处理");

final List<Choice> _choices = <Choice>[
  Choice(title: '原始事件监听', builder: Builder(builder: (context) => TouchEventListenerWidget())),

  Choice(title: 'behavior.deferToChild 属性', builder: Builder(builder: (context) =>
      HitTestBehaviorWidget())),

  Choice(title: 'behavior.opaque 属性', builder: Builder(builder: (context) =>
      HitTestBehaviorOpaqueWidget())),

  Choice(title: 'behavior.translucent 属性', builder: Builder(builder: (context) =>
      HitTestBehaviorTranslucentWidget())),

  Choice(title: 'IgnorePointer', builder: Builder(builder: (context) =>
      IgnoreEventsWidget())),

  Choice(title: 'AbsorbPointer', builder: Builder(builder: (context) =>
      AbortEventsWidget())),

];

class HitTestBehaviorOpaqueWidget extends StatelessWidget {

  @override
  Widget build(BuildContext context) {
    return Listener(
        child: ConstrainedBox(
          constraints: BoxConstraints.tight(Size(300.0, 150.0)),
          child: Center(child: Text("Box A")),
        ),
        //opaque：在命中测试时，将当前组件当成不透明处理(即使本身是透明的)，最终的效果相当于当前Widget的整个区域都是点击区域。
        //因为 deferToChild 会去子组件判断是否命中测试，只有点击文本内容区域才会触发点击事件，而该例中子组件就是 Text("Box A") 。
        // 如果我们想让整个300×150的矩形区域都能点击我们可以将behavior设为HitTestBehavior.opaque。注意，该属性并不能用于在组件树中拦截（忽略）事件，它只是决定命中测试时的组件大小。
        behavior: HitTestBehavior.opaque,
        onPointerDown: (event) => print("down A")
    );
  }
}

class HitTestBehaviorTranslucentWidget extends StatelessWidget {

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: <Widget>[
        Listener(
          child: ConstrainedBox(
            constraints: BoxConstraints.tight(Size(300.0, 200.0)),
            child: DecoratedBox(
                decoration: BoxDecoration(color: Colors.blue)),
          ),
          onPointerDown: (event) => print("down0"),
        ),

        Listener(
          child: ConstrainedBox(
            constraints: BoxConstraints.tight(Size(200.0, 100.0)),
            child: Center(child: Text("左上角200*100范围内非文本区域点击")),
          ),
          onPointerDown: (event) => print("down1"),
          //translucent：当点击组件透明区域时，可以对自身边界内及底部可视区域都进行命中测试，这意味着点击顶部组件透明区域时，
          // 顶部组件和底部组件都可以接收到事件，例如：
          behavior: HitTestBehavior.translucent,
        )
      ],
    );
  }

}

class HitTestBehaviorWidget extends StatelessWidget {

  @override
  Widget build(BuildContext context) {
    return Listener(
        child: ConstrainedBox(
          constraints: BoxConstraints.tight(Size(300.0, 150.0)),
          child: Center(child: Text("Box A")),
        ),
        //默认属性 deferToChild：子组件会一个接一个的进行命中测试，如果子组件中有测试通过的，则当前组件通过，
        // 这就意味着，如果指针事件作用于子组件上时，其父级组件也肯定可以收到该事件。
        behavior: HitTestBehavior.deferToChild,
        onPointerDown: (event) => print("down A")
    );
  }

}

class TouchEventListenerWidget extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return TouchEventListenerWidgetState();
  }
}

class TouchEventListenerWidgetState extends State<TouchEventListenerWidget> {
  //定义一个状态，保存当前指针位置
  PointerEvent _event;

  @override
  Widget build(BuildContext context) {
    return Listener(
      child: Container(
        alignment: Alignment.center,
        color: Colors.blue,
        width: 300.0,
        height: 150.0,
        child: Text(_event?.toString() ?? "", style: TextStyle(color: Colors.white)),
      ),
      onPointerDown: (PointerDownEvent event) => setState(() => _event = event),
      onPointerMove: (PointerMoveEvent event) => setState(() => _event = event),
      onPointerUp: (PointerUpEvent event) => setState(() => _event = event),
    );
  }

}

//假如我们不想让某个子树响应PointerEvent的话，我们可以使用IgnorePointer和AbsorbPointer---------------------------------
//IgnorePointer本身不会参与命中测试
class IgnoreEventsWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Listener(
      child: IgnorePointer(
        child: Listener(
          child: Container(
            color: Colors.red,
            width: 200.0,
            height: 100.0,
          ),
          onPointerDown: (event) => print("in"),
        ),
      ),
      onPointerDown: (event) => print("up"),
    );
  }
}

//AbsorbPointer本身会参与命中测试
class AbortEventsWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Listener(
      child: AbsorbPointer(
        child: Listener(
          child: Container(
            color: Colors.red,
            width: 200.0,
            height: 100.0,
          ),
          onPointerDown: (event) => print("in"),
        ),
      ),
      onPointerDown: (event) => print("up"),
    );
  }
}