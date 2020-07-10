import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

Widget buildStoreBySpDemo() =>
    Scaffold(
      appBar: AppBar(title: Text("Store By Sp"),),
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


  void _store() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.setString("key", _controller.text)
        .then((onValue) {
      print("store success");
    });
  }

  void _read() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    var value = (prefs.getString('key') ?? 'null');
    setState(() {
      _value = value;
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