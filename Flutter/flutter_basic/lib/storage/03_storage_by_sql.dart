import 'package:flutter/material.dart';
import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';

Widget buildStoreByDBDemo() =>
    Scaffold(
      appBar: AppBar(title: Text("Store By DB"),),
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

  int studentID = 123;

  Database _database;

  Future<Database> get db async {
    if (_database != null) {
      return _database;
    }
    _database = await _initDatabase();

    return _database;
  }

  Future<Database> _initDatabase() async {
    return openDatabase(
      join(await getDatabasesPath(), 'students_database.db'),
      onCreate: (db, version) =>
          db
              .execute("CREATE TABLE students(id TEXT PRIMARY KEY, name TEXT, score INTEGER)"),
      onUpgrade: (db, oldVersion, newVersion) {
        //dosth for migration
        print("old:$oldVersion,new:$newVersion");
      },
      version: 1,
    );
  }

  Future<void> insertStudent(Student std) async {
    final Database database = await db;
    await database.insert(
      'students',
      std.toJson(),
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
  }

  Future<List<Student>> students() async {
    final Database database = await db;
    final List<Map<String, dynamic>> maps = await database.query('students');
    return List.generate(maps.length, (i) => Student.fromJson(maps[i]));
  }

  void _store() async {
    var student1 = Student(id: '${++studentID}', name: '张三', score: 90);
    var student2 = Student(id: '${++studentID}', name: '李四', score: 80);
    var student3 = Student(id: '${++studentID}', name: '王五', score: 85);

    await insertStudent(student1);
    await insertStudent(student2);
    await insertStudent(student3);
  }

  void _read() async {
    await students().then((studentList) {
      setState(() {
        _value = studentList.join(",");
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
          Padding(padding: EdgeInsets.all(20), child: Text(_value),),
          //2
          RaisedButton(onPressed: () => _store(), child: Text("store"),),
          //3
          RaisedButton(onPressed: () => _read(), child: Text("read"),)
        ],
      )
      ,
    );
  }

  @override
  void dispose() async {
    (await db).close();
    super.dispose();
  }

}

class Student {
  String id;
  String name;
  int score;

  Student({this.id, this.name, this.score,});

  factory Student.fromJson(Map<String, dynamic> parsedJson){
    return Student(
      id: parsedJson['id'],
      name: parsedJson['name'],
      score: parsedJson ['score'],
    );
  }

  Map<String, dynamic> toJson() {
    return {'id': id, 'name': name, 'score': score,};
  }

  @override
  String toString() {
    return 'Student{id: $id, name: $name, score: $score}';
  }

}