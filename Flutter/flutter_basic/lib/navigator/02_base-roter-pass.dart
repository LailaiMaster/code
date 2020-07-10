import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

/*
*下面展示的是使用基本路由进行参数传递。 
*/

class Todo {
  final String title;
  final String description;

  Todo(this.title, this.description);
}

Widget buildBaseRouterPassWidget() {
  return new MaterialApp(
    title: 'Passing Data',
    home: new _TodoScreen(
      todoList: new List.generate(
        20,
        (i) => new Todo(
          'Todo $i',
          'A description of what needs to be done for Todo $i',
        ),
      ),
    ),
  );
}

class _TodoScreen extends StatelessWidget {
  final List<Todo> todoList;

  _TodoScreen({Key key, @required this.todoList}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text('Todos'),
      ),
      body: new ListView.builder(
        itemCount: todoList.length,
        itemBuilder: (context, index) {
          return new ListTile(
            title: new Text(todoList[index].title),
            // When a user taps on the ListTile, navigate to the DetailScreen.
            // Notice that we're not only creating a new DetailScreen, we're
            // also passing the current todo through to it!
            onTap: () {
              Navigator.push(
                context,
                new MaterialPageRoute(
                  builder: (context) =>
                      new _DetailScreen(todo: todoList[index]),
                ),
              );
            },
          );
        },
      ),
    );
  }
}

class _DetailScreen extends StatelessWidget {
  // Declare a field that holds the Todo
  final Todo todo;

  // In the constructor, require a Todo
  _DetailScreen({Key key, @required this.todo}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    // Use the Todo to create our UI
    return new Scaffold(
      appBar: new AppBar(
        title: new Text("${todo.title}"),
      ),
      body: new Padding(
        padding: new EdgeInsets.all(16.0),
        child: new Text('${todo.description}'),
      ),
    );
  }
}
