import 'dart:io';

import 'package:flutter/material.dart';
import 'package:path_provider/path_provider.dart';

Widget buildStoreByFilesDemo() =>
    Scaffold(
      appBar: AppBar(title: Text("Store By File"),),
      body: StoreByFilesDemoWidget(),
    );

class StoreByFilesDemoWidget extends StatefulWidget {

  @override
  State<StatefulWidget> createState() {
    return StoreByFilesDemoWidgetState();
  }

}

class StoreByFilesDemoWidgetState extends State<StoreByFilesDemoWidget> {

  String _value = "";

  final TextEditingController _controller = new TextEditingController();

  Future<File> get _localFile async {
    final directory = await getApplicationDocumentsDirectory();
    final path = directory.path;
    return File('$path/content.txt');
  }

  void _store() async {
    var file = await _localFile;
    file.writeAsString(_controller.text)
        .then((onValue) {
      print("store success");
    });
  }

  void _read() async {
    var file = await _localFile;
    file.readAsString()
        .then((onValue) {
      setState(() {
        _value = onValue;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return ConstrainedBox(
      constraints: BoxConstraints(minWidth: double.infinity),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: <Widget>[
          //1
          TextField(
            controller: _controller,
            decoration: InputDecoration(
                hintText: 'Enter a search term',
                contentPadding: EdgeInsets.symmetric(horizontal: 10)
            ),
          ),
          //2
          Padding(padding: EdgeInsets.all(20), child: Text(_value),),
          //3
          RaisedButton(onPressed: () => _store(), child: Text("store"),),
          //4
          RaisedButton(onPressed: () => _read(), child: Text("read"),)
        ],
      )
      ,
    );
  }

}