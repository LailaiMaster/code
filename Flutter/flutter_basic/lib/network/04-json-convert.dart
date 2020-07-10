import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'dart:convert';

Widget buildJsonParsingDemo() => Scaffold(body: JsonParsingWidget());

class JsonParsingWidget extends StatelessWidget {

  final String _jsonString = '''
    {
      "id":"123",
      "name":"张三",
      "score" : 95,
      "teacher": 
         {
           "name": "李四",
           "age" : 40
         }
    }
    ''';

  static Student _parseStudent(String content) {
    final jsonResponse = json.decode(content);
    Student student = Student.fromJson(jsonResponse);
    return student;
  }

  Future<Student> _loadStudent() {
    return compute(_parseStudent, _jsonString);
  }

  _jsonParseDemo() {
    _loadStudent().then((s) {
      String content = '''
        name: ${s.name}
        score:${s.score}
        teacher:${s.teacher.name}
        ''';
      print(content);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: RaisedButton(
          child: Text("Click to parse json"),
          onPressed: () => _jsonParseDemo()),
    );
  }

}

class Student {

  String id;
  String name;
  int score;
  Teacher teacher;

  Student({this.id, this.name, this.score, this.teacher});

  factory Student.fromJson(Map<String, dynamic> parsedJson){
    return Student(
        id: parsedJson['id'],
        name: parsedJson['name'],
        score: parsedJson ['score'],
        teacher: Teacher.fromJson(parsedJson['teacher'])
    );
  }

}

class Teacher {

  String name;
  int age;

  Teacher({this.name, this.age});

  factory Teacher.fromJson(Map<String, dynamic> parsedJson){
    return Teacher(
        name: parsedJson['name'],
        age: parsedJson ['age']
    );
  }

}