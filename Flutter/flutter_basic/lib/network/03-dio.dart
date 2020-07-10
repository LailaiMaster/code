import 'package:flutter/material.dart';
import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';

//[dio](https://github.com/flutterchina/dio) 是一个强大易用的dart http请求库。
Widget buildDioDemoWidget() => MyHomePage(title: "DioSample",);

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {

  dioDemo() async {
    try {
      Dio dio = new Dio();
      var response = await dio
          .get("https://flutter.dev", options: Options(headers: {
        "user-agent": "Custom-UA"
      }));
      print(response.data.toString());
    }
    catch (e) {
      print('Error:$e');
    }
  }

  dioParallelDemo() async {
    try {
      Dio dio = new Dio();
      List<Response> responseX = await Future.wait([
        dio.get("https://flutter.dev"),
        dio.get("https://pub.dev/packages/dio")
      ]);

      //打印请求1响应结果
      print("Response1: ${responseX[0].toString()}");
      //打印请求2响应结果
      print("Response2: ${responseX[1].toString()}");
    }
    catch (e) {
      print('Error:$e');
    }
  }

  dioInterceptorReject() async {
    Dio dio = new Dio();
    dio.interceptors.add(InterceptorsWrapper(
        onRequest: (RequestOptions options) {
          return dio.reject("Error：拦截的原因");
        }
    ));
    try {
      var response = await dio.get("https://flutter.dev");
      print(response.data.toString());
    } catch (e) {
      print('Error:$e');
    }
  }

  dioIntercepterCache() async {
    Dio dio = new Dio();
    dio.interceptors.add(InterceptorsWrapper(
        onRequest: (RequestOptions options) {
          return dio.resolve("返回缓存数据");
        }
    ));
    try {
      var response = await dio.get("https://flutter.dev");
      print(response.data.toString());
    } catch (e) {
      print('Error:$e');
    }
  }

  dioIntercepterCustomHeader() async {
    Dio dio = new Dio();
    dio.interceptors.add(InterceptorsWrapper(
        onRequest: (RequestOptions options) {
          options.headers["user-agent"] = "Custom-UA";
          return options;
        }
    ));

    try {
      var response = await dio.get("https://flutter.dev");
      print(response.data.toString());
    } catch (e) {
      print('Error:$e');
    }
  }

  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called, for instance as done
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(

        child: Column(
          children: <Widget>[

            RaisedButton(
              child: Text('Dio demo'),
              onPressed: () => dioDemo(),
            ),
            RaisedButton(
              child: Text('Dio 并发demo'),
              onPressed: () => dioParallelDemo(),
            ),
            RaisedButton(
              child: Text('Dio 拦截'),
              onPressed: () => dioInterceptorReject(),
            ),
            RaisedButton(
              child: Text('Dio 缓存'),
              onPressed: () => dioIntercepterCache(),
            ),
            RaisedButton(
              child: Text('Dio 自定义header'),
              onPressed: () => dioIntercepterCustomHeader(),
            ),
          ],
        ),
      ),
      // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}

