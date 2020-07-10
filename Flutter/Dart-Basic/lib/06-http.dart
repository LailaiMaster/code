import 'dart:io';
import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:dio/dio.dart';

void doRequest() {
//  _requestByHttpClient();
//  _requestByHttp();
  _requestByDio();
}

void _requestByHttpClient() async {
  // 创建网络调用示例
  var httpClient = HttpClient();
  //设置通用请求行为 (超时时间)
  httpClient.idleTimeout = Duration(seconds: 5);

  // 构造 URI，设置 user-agent 为 "Custom-UA"
  var uri = Uri.parse('https://flutter.dev');
  var request = await httpClient.getUrl(uri);
  request.headers.add('user-agent', 'Custom-UA');

  // 发起请求，等待响应
  var response = await request.close();

  // 收到响应，打印结果
  if (response.statusCode == HttpStatus.ok) {
    print(await response.transform(utf8.decoder).join());
  } else {
    print('Error: \nHttp status ${response.statusCode}');
  }
}

void _requestByHttp() async {
  // 创建网络调用示例
  var client = http.Client();

  // 构造 URI
  var uri = Uri.parse('https://www.baudu.com');

  // 设置 user-agent 为 "Custom-UA"，随后立即发出请求
  var response = await client.get(uri, headers: {'user-agent': 'Custom-UA'});

  // 打印请求结果
  if (response.statusCode == HttpStatus.ok) {
    print(response.body);
  } else {
    print('Error: ${response.statusCode}');
  }
}

void _requestByDio() async{
  // 创建网络调用示例
  var dio = Dio();

  // 设置 URI 及请求 user-agent 后发起请求
  var response = await dio.get('https://flutter.dev', options:Options(headers: {'user-agent' : 'Custom-UA'}));

  // 打印请求结果
  if(response.statusCode == HttpStatus.ok) {
    print(response.data.toString());
  } else {
    print('Error: ${response.statusCode}');
  }
}
