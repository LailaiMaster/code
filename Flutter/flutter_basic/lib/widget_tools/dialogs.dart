import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

Widget buildDialogSampleWidget() =>
    Scaffold(
      appBar: AppBar(title: Text("Dialogs"),),
      body: DialogsWidget(),
    );

class ListItem {
  final String itemName;
  final ValueChanged<BuildContext> action;

  ListItem(this.itemName, this.action);
}

class DialogsWidget extends StatelessWidget {

  final List<ListItem> dialogTypes = [
    ListItem("SnackBar", _showSnackBar),
    ListItem("AlertDialog", _showAlertDialog),
    ListItem("SimpleDialog", _showShortListDialog),
    ListItem("ListDialog", _showListDialog),
    ListItem("CustomDialog", _showCustomAlertDialog),
    ListItem("DeleteConfirmDialogWithCheckBox1", _showDeleteConfirmDialogWithCheckBox1),
    ListItem("DeleteConfirmDialogWithCheckBox2", _showDeleteConfirmDialogWithCheckBox2),
    ListItem("DeleteConfirmDialogWithCheckBox3", _showDeleteConfirmDialogWithCheckBox3),
    ListItem("ModalBottomSheet", _showModalBottomSheet),
    ListItem("BottomSheet", _showBottomSheet),
    ListItem("Loading", _showLoadingDialog),
    ListItem("LoadingSmall", _showLoadingDialogCustomSize),
    ListItem("DatePickerAndroid", _showDatePickerAndroid),
    ListItem("DatePickerIOS", _showDatePickerIOS)
  ];

  static void _showSnackBar(BuildContext context) {
    Scaffold.of(context)
        .showSnackBar(new SnackBar(content: new Text("Snack by Flutter")));
  }

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
        itemCount: dialogTypes.length,
        itemBuilder: (context, index) {
          var listItem = dialogTypes[index];
          return Container(
              padding: EdgeInsets.symmetric(vertical: 5),
              child: RaisedButton(
                  child: Text(listItem.itemName),
                  onPressed: () {
                    listItem.action(context);
                  }
              )
          );
        }
    );
  }

}

//AlertDialog 和 SimpleDialog 的使用------------------------------------------------------------

// 弹出对话框
Future<bool> _showAlertDialog(BuildContext context) {
  return showDialog<bool>(
    context: context,
    builder: (context) {
      return AlertDialog(

        title: Text("提示"),

        content: Text("您确定要删除当前文件吗?"),

        actions: <Widget>[
          FlatButton(
            child: Text("取消"),
            onPressed: () => Navigator.of(context).pop(), // 关闭对话框
          ),
          FlatButton(
            child: Text("删除"),
            onPressed: () {
              //关闭对话框并返回true
              Navigator.of(context).pop(true);
            },
          ),
        ],

      );
    },
  );
}

Future<int> _showShortListDialog(BuildContext context) {
  return showDialog<int>(
      context: context,
      builder: (BuildContext context) {
        return SimpleDialog(
          title: const Text('请选择语言'),

          children: <Widget>[

            SimpleDialogOption(
              onPressed: () {
                // 返回1
                Navigator.pop(context, 1);
              },
              child: Padding(
                padding: const EdgeInsets.symmetric(vertical: 6),
                child: const Text('中文简体'),
              ),
            ),

            SimpleDialogOption(
              onPressed: () {
                // 返回2
                Navigator.pop(context, 2);
              },
              child: Padding(
                padding: const EdgeInsets.symmetric(vertical: 6),
                child: const Text('美国英语'),
              ),
            ),

          ],
        );
      });
}

//Dialog 的使用------------------------------------------------------------

Future<int> _showListDialog(BuildContext context) {
  return showDialog<int>(
    context: context,
    builder: (BuildContext context) {
      var child = Column(
        children: <Widget>[

          ListTile(title: Text("请选择")),

          Expanded(
              child: ListView.builder(
                itemCount: 30,
                itemBuilder: (BuildContext context, int index) {
                  return ListTile(
                    title: Text("$index"),
                    onTap: () => Navigator.of(context).pop(index),
                  );
                },
              )),

        ],
      );
      //使用AlertDialog会报错：实际上AlertDialog和SimpleDialog都使用了Dialog类。
      // 由于AlertDialog和SimpleDialog中使用了IntrinsicWidth来尝试通过子组件的实际尺寸来调整自身尺寸，
      // 这就导致他们的子组件不能是延迟加载模型的组件（如ListView、GridView 、 CustomScrollView等）
      //return AlertDialog(content: child);
      return Dialog(child: child);
    },
  );
}

//showGeneralDialog 的使用------------------------------------------------------------

Future<T> _showCustomDialog<T>({
  @required BuildContext context,
  bool barrierDismissible = true,
  WidgetBuilder builder,
}) {
  final ThemeData theme = Theme.of(context, shadowThemeOnly: true);

  return showGeneralDialog(
    context: context,
    pageBuilder: (BuildContext buildContext, Animation<double> animation, Animation<
        double> secondaryAnimation) {
      final Widget pageChild = Builder(builder: builder);
      return SafeArea(
        child: Builder(builder: (BuildContext context) {
          return theme != null ? Theme(data: theme, child: pageChild) : pageChild;
        }),
      );
    },

    barrierDismissible: barrierDismissible,
    barrierLabel: MaterialLocalizations
        .of(context)
        .modalBarrierDismissLabel,
    barrierColor: Colors.black87, // 自定义遮罩颜色
    transitionDuration: const Duration(milliseconds: 150),

    transitionBuilder: _buildMaterialDialogTransitions,
  );
}

Widget _buildMaterialDialogTransitions(BuildContext context,
    Animation<double> animation,
    Animation<double> secondaryAnimation,
    Widget child) {
  // 使用缩放动画
  return ScaleTransition(
    scale: CurvedAnimation(
      parent: animation,
      curve: Curves.easeOut,
    ),
    child: child,
  );
}

Future<bool> _showCustomAlertDialog(BuildContext context) {
  return _showCustomDialog<bool>(
    context: context,
    builder: (context) {
      return AlertDialog(
        title: Text("提示"),

        content: Text("您确定要删除当前文件吗?"),

        actions: <Widget>[
          FlatButton(
            child: Text("取消"),
            onPressed: () => Navigator.of(context).pop(),
          ),
          FlatButton(
            child: Text("删除"),
            onPressed: () {
              // 执行删除操作
              Navigator.of(context).pop(true);
            },
          ),
        ],

      );
    },
  );
}

//对话框状态管理------------------------------------------------------------

//对话框状态管理1：单独封装一个内部管理选中状态的复选框组件
class DialogCheckbox extends StatefulWidget {

  DialogCheckbox({
    Key key,
    this.value,
    @required this.onChanged,
  });

  final ValueChanged<bool> onChanged;
  final bool value;

  @override
  _DialogCheckboxState createState() => _DialogCheckboxState();
}

class _DialogCheckboxState extends State<DialogCheckbox> {
  bool value;

  @override
  void initState() {
    value = widget.value;
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Checkbox(
      value: value,
      onChanged: (v) {
        //将选中状态通过事件的形式抛出
        widget.onChanged(v);
        setState(() {
          //更新自身选中状态
          value = v;
        });
      },
    );
  }
}

Future<bool> _showDeleteConfirmDialogWithCheckBox1(BuildContext context) {
  bool _withTree = false; //记录复选框是否选中

  return showDialog<bool>(
    context: context,
    builder: (context) {
      return AlertDialog(

        title: Text("提示"),

        content: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisSize: MainAxisSize.min,
          children: <Widget>[
            Text("您确定要删除当前文件吗?"),

            Row(
              children: <Widget>[

                Text("同时删除子目录？"),

                DialogCheckbox(
                  value: _withTree, //默认不选中
                  onChanged: (bool value) {
                    //更新选中状态
                    _withTree = !_withTree;
                  },
                ),

              ],
            ),
          ],
        ),

        actions: <Widget>[
          FlatButton(
            child: Text("取消"),
            onPressed: () => Navigator.of(context).pop(),
          ),
          FlatButton(
            child: Text("删除"),
            onPressed: () {
              // 将选中状态返回
              Navigator.of(context).pop(_withTree);
            },
          ),
        ],

      );
    },
  );
}

//对话框状态管理2：使用StatefulBuilder方法
class StatefulBuilder extends StatefulWidget {
  const StatefulBuilder({
    Key key,
    @required this.builder,
  })
      : assert(builder != null),
        super(key: key);

  final StatefulWidgetBuilder builder;

  @override
  _StatefulBuilderState createState() => _StatefulBuilderState();
}

class _StatefulBuilderState extends State<StatefulBuilder> {
  @override
  Widget build(BuildContext context) => widget.builder(context, setState);
}

Future<bool> _showDeleteConfirmDialogWithCheckBox2(BuildContext context) {
  bool _withTree = false; //记录复选框是否选中

  return showDialog<bool>(
    context: context,
    builder: (context) {
      return AlertDialog(

        title: Text("提示"),

        content: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisSize: MainAxisSize.min,
          children: <Widget>[
            Text("您确定要删除当前文件吗?"),

            Row(
              children: <Widget>[

                Text("同时删除子目录？"),

                //使用StatefulBuilder来构建StatefulWidget上下文
                StatefulBuilder(builder: (context, _setState) {
                  return Checkbox(value: _withTree, onChanged: (bool value) {
                    _setState(() {
                      _withTree = !_withTree;
                    });
                  });
                })

              ],
            ),
          ],
        ),

        actions: <Widget>[
          FlatButton(
            child: Text("取消"),
            onPressed: () => Navigator.of(context).pop(),
          ),
          FlatButton(
            child: Text("删除"),
            onPressed: () {
              // 将选中状态返回
              Navigator.of(context).pop(_withTree);
            },
          ),
        ],

      );
    },
  );
}

//对话框状态管理3：context实际上就是Element对象的引用
Future<bool> _showDeleteConfirmDialogWithCheckBox3(BuildContext context) {
  bool _withTree = false;

  return showDialog<bool>(
    context: context,
    builder: (context) {
      return AlertDialog(

        title: Text("提示"),

        content: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisSize: MainAxisSize.min,
          children: <Widget>[

            Text("您确定要删除当前文件吗?"),

            Row(
              children: <Widget>[
                Text("同时删除子目录？"),

                /*Checkbox( // 依然使用Checkbox组件
                  value: _withTree,
                  onChanged: (bool value) {
                    // 此时context为对话框UI的根Element，我们
                    // 直接将对话框UI对应的Element标记为dirty
                    (context as Element).markNeedsBuild();
                    _withTree = !_withTree;
                  },
                ),*/

                // 通过Builder来获得构建Checkbox的`context`， 这是一种常用的缩小`context`范围的方式
                Builder(
                  builder: (BuildContext context) {
                    return Checkbox(
                      value: _withTree,
                      onChanged: (bool value) {
                        (context as Element).markNeedsBuild();
                        _withTree = !_withTree;
                      },
                    );
                  },)

              ],
            ),

          ],
        ),

        actions: <Widget>[
          FlatButton(
            child: Text("取消"),
            onPressed: () => Navigator.of(context).pop(),
          ),
          FlatButton(
            child: Text("删除"),
            onPressed: () {
              // 执行删除操作
              Navigator.of(context).pop(_withTree);
            },
          ),

        ],

      );
    },
  );
}

//弹出底部菜单列表模态对话框------------------------------------------------------------
Future<int> _showModalBottomSheet(BuildContext context) {
  return showModalBottomSheet<int>(
    context: context,
    builder: (BuildContext context) {
      return ListView.builder(
        itemCount: 30,
        itemBuilder: (BuildContext context, int index) {
          return ListTile(
            title: Text("$index"),
            onTap: () => Navigator.of(context).pop(index),
          );
        },
      );
    },
  );
}

//弹出底部菜单列表模态对话框------------------------------------------------------------
// 返回的是一个controller，showBottomSheet和我们上面介绍的弹出对话框的方法原理不同：
// showBottomSheet是调用widget树顶部的Scaffold组件的ScaffoldState的showBottomSheet同名方法实现，
// 也就是说要调用showBottomSheet方法就必须得保证父级组件中有Scaffold。
PersistentBottomSheetController<int> _showBottomSheet(BuildContext context) {
  return showBottomSheet<int>(
    context: context,
    builder: (BuildContext context) {
      return ListView.builder(
        itemCount: 30,
        itemBuilder: (BuildContext context, int index) {
          return ListTile(
            title: Text("$index"),
            onTap: () {
              // do something
              print("$index");
              Navigator.of(context).pop();
            },
          );
        },
      );
    },
  );
}

//Loading------------------------------------------------------------
_showLoadingDialog(BuildContext context) {
  showDialog(
    context: context,
    barrierDismissible: false, //点击遮罩不关闭对话框
    builder: (context) {
      return AlertDialog(
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: <Widget>[
            CircularProgressIndicator(),
            Padding(
              padding: const EdgeInsets.only(top: 26.0),
              child: Text("正在加载，请稍后..."),
            )
          ],
        ),
      );
    },
  );
}

//想自定义对话框宽度，这时只使用SizedBox或ConstrainedBox是不行的，原因是showDialog中已经给对话框设置了宽度限制，
//我们可以使用UnconstrainedBox先抵消showDialog对宽度的限制，然后再使用SizedBox指定宽度
_showLoadingDialogCustomSize(BuildContext context) {
  showDialog(
    context: context,
    barrierDismissible: false, //点击遮罩不关闭对话框
    builder: (context) {
      return UnconstrainedBox(
        constrainedAxis: Axis.vertical,
        child: SizedBox(
          width: 280,
          child: AlertDialog(
            content: Column(
              mainAxisSize: MainAxisSize.min,
              children: <Widget>[
                CircularProgressIndicator(),
                Padding(
                  padding: const EdgeInsets.only(top: 26.0),
                  child: Text("正在加载，请稍后..."),
                )
              ],
            ),
          ),
        ),
      );
    },
  );
}

//DatePicker------------------------------------------------------------
Future<DateTime> _showDatePickerAndroid(BuildContext context) {
  var date = DateTime.now();
  return showDatePicker(
    context: context,
    initialDate: date,
    firstDate: date,
    lastDate: date.add( //未来30天可选
      Duration(days: 30),
    ),
  );
}

Future<DateTime> _showDatePickerIOS(BuildContext context) {
  var date = DateTime.now();
  return showCupertinoModalPopup(
    context: context,
    builder: (ctx) {
      return SizedBox(
        height: 200,
        child: CupertinoDatePicker(
          mode: CupertinoDatePickerMode.dateAndTime,
          minimumDate: date,
          maximumDate: date.add(
            Duration(days: 30),
          ),
          maximumYear: date.year + 1,
          onDateTimeChanged: (DateTime value) {
            print(value);
          },
        ),
      );
    },
  );
}