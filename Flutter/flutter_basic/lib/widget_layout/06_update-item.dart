import 'package:flutter/material.dart';

Widget buildUpdateItemWidget() {
  return Theme(
      data: ThemeData(
        primaryColor: Colors.lightBlue[800], //主题色为蓝色
      ),

      child: Scaffold(
          appBar: AppBar(
            title: Text("Update Item"),
          ),
          body: UpdatedItemWidget(model: _buildUpdatedItemModel())));
}

UpdatedItemModel _buildUpdatedItemModel() {
  return UpdatedItemModel(
      appIcon: "images/icon.png",
      appDescription:
      "Thanks for using Google Maps! This release brings bug fixes that improve our product to help you discover new places and navigate to them.",
      appName: "Google Maps - Transit & Fond",
      appSize: "137.2",
      appVersion: "Version 5.19",
      appDate: "2019年6月5日");
}

class UpdatedItemModel {
  String appIcon;
  String appName;
  String appSize;
  String appDate;
  String appDescription;
  String appVersion;

  UpdatedItemModel({this.appIcon,
    this.appName,
    this.appSize,
    this.appDate,
    this.appDescription,
    this.appVersion});
}

class UpdatedItemWidget extends StatelessWidget {
  final UpdatedItemModel model;

  UpdatedItemWidget({Key key, this.model, this.onPressed}) : super(key: key);

  final VoidCallback onPressed;

  @override
  Widget build(BuildContext context) {
    //组合上下两部分
    return Column(
        children: <Widget>[buildTopRow(context), buildBottomRow(context)]);
  }

  //创建上半部分
  Widget buildTopRow(BuildContext context) {
    return Row(children: <Widget>[
      Padding(
          padding: EdgeInsets.all(10),
          child: ClipRRect(
              borderRadius: BorderRadius.circular(8.0),
              child: Image.asset(model.appIcon, width: 80, height: 80))),
      Expanded(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            Text(
              model.appName,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              style: TextStyle(fontSize: 20, color: Color(0xFF8E8D92)),
            ),
            Text(
              "${model.appDate}",
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              style: TextStyle(fontSize: 16, color: Color(0xFF8E8D92)),
            ),
          ],
        ),
      ),
      Padding(
        padding: EdgeInsets.fromLTRB(0, 0, 10, 0),
        child: FlatButton(
          color: Color(0xFFF1F0F7),
          highlightColor: Colors.blue[700],
          colorBrightness: Brightness.dark,
          child: Text(
            "OPEN",
            style: TextStyle(
                color: Color(0xFF007AFE), fontWeight: FontWeight.bold),
          ),
          shape:
          RoundedRectangleBorder(borderRadius: BorderRadius.circular(20.0)),
          onPressed: onPressed,
        ),
      )
    ]);
  }

  //创建下半部分
  Widget buildBottomRow(BuildContext context) {
    return Padding(
        padding: EdgeInsets.fromLTRB(15, 0, 15, 0),
        child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              Text(model.appDescription),
              Padding(
                  padding: EdgeInsets.fromLTRB(0, 10, 0, 0),
                  child: Text("${model.appVersion} • ${model.appSize} MB"))
            ]));
  }
}
