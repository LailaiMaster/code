import 'dart:io';
import 'dart:convert';
import 'package:flutter/material.dart';

Widget buildHttpClientWidget() => Scaffold(
      appBar: AppBar(
        title: Text('HttpClient Demo'),
      ),
      body: HttpClientDemo(),
    );

class HttpClientDemo extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return HttpClientDemoState();
  }
}

class HttpClientDemoState extends State<HttpClientDemo> {
  String _result = "nothing";

  void httpClientRequest() async {
    try {
      var httpClient = HttpClient();
      httpClient.idleTimeout = Duration(seconds: 5);
      var uri = Uri.parse("https://flutter.dev");
      var request = await httpClient.getUrl(uri);
      request.headers.add("user-agent", "Custom-UA");
      var response = await request.close();
      print('Respone code: ${response.statusCode}');
      _result = await response.transform(utf8.decoder).join();
      setState(() {});
    } catch (e) {
      _result = e.toString();
      setState(() {});
    }
  }

  @override
  Widget build(BuildContext context) {
    return ConstrainedBox(
      constraints: BoxConstraints(minWidth: double.infinity),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: <Widget>[
          FlatButton(
            child: Text('do request'),
            onPressed: () => {
              setState(() {
                _result = "requesting";
                httpClientRequest();
              })
            },
          ),
          //这里要包一层：https://stackoverflow.com/questions/56988283/bottom-overflowed-by-infinity-pixels/56994695
          Expanded(
            flex: 1,
            child: SingleChildScrollView(
              child: Padding(
                padding: EdgeInsets.all(20),
                child: Text(_result),
              ),
            ),
          )
        ],
      ),
    );
  }
}
