import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

Widget buildSimpleList() =>
    Scaffold(

      //顶部Bar
      appBar: new AppBar(
        title: new Text('Basic List'),
      ),

      //列表体
      body: new ListView(
        children: <Widget>[
          // ListTile 是一个方便我们开始开发列表页面的简单列表 Item。
          new ListTile(
            leading: new Icon(Icons.map),
            title: new Text('Map'),
          ),
          new ListTile(
            leading: new Icon(Icons.photo),
            title: new Text('Album'),
          ),
          new ListTile(
            leading: new Icon(Icons.phone),
            title: new Text('Phone'),
          ),
        ],
      ),

    );
