import 'package:flutter/material.dart';

class Page {
  String name;
  WidgetBuilder builder;

  Page(this.name, this.builder);
}

buildListBody(String title, BuildContext context, List<Page> pages) {
  return Scaffold(
      appBar: AppBar(
        title: Text(title),
      ),

      body: GridView.builder(
        itemCount: pages.length,
        padding: EdgeInsets.all(10),
        itemBuilder: (context, index) {
          return Container(
            margin: EdgeInsets.all(5),
            child: RaisedButton(
                child: Text(pages[index].name, style: TextStyle(fontSize: 12),),
                onPressed: () {
                  Navigator
                      .push(context, MaterialPageRoute(builder: pages[index]
                      .builder));
                  print("clicked-----------------------------------${pages[index]
                      .builder}");
                }),
          );
        },

        gridDelegate: new SliverGridDelegateWithFixedCrossAxisCount(crossAxisCount: 3),
      ));
}

class SampleWidget extends StatefulWidget {

  final List<Choice> choices;
  final String title;

  const SampleWidget(this.choices, this.title, {Key key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return SampleWidgetState();
  }
}

class Choice {
  const Choice({this.title, this.builder});

  final String title;
  final Builder builder;
}

class SampleWidgetState extends State<SampleWidget> {

  Builder _builder;

  void _select(Choice choice) {
    // Causes the app to rebuild with the new _selectedChoice.
    setState(() {
      _builder = choice.builder;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text(widget.title),
          actions: <Widget>[
            PopupMenuButton<Choice>(
              onSelected: _select,
              itemBuilder: (BuildContext context) {
                return widget.choices.map((Choice choice) {
                  return PopupMenuItem<Choice>(
                    value: choice,
                    child: Text(choice.title),
                  );
                }).toList();
              },
            ),
          ],
        ),
        body: _builder ?? widget.choices[0].builder
    );
  }

}