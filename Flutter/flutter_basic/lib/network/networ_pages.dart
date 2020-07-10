import 'package:flutter/material.dart';
import 'package:flutter_basic/common.dart';

//网络
import 'package:flutter_basic/network/01_httpclient.dart';
import 'package:flutter_basic/network/02_http_sample1.dart';
import 'package:flutter_basic/network/02_http_sample2.dart';
import 'package:flutter_basic/network/03-dio.dart';
import 'package:flutter_basic/network/04-json-convert.dart';
import 'package:flutter_basic/network/07_web_socket.dart';

List<Page> _buildRoutes() {
  return [
    //net work
    Page("HttpClient", (context) => buildHttpClientWidget()),
    Page("Http Sampe1", (context) => buildHttpSample1Widget()),
    Page("Http Sampe2", (context) => buildHttpSample2Widget()),
    Page("Dio Sampe", (context) => buildDioDemoWidget()),
    Page("Json Parse", (context) => buildJsonParsingDemo()),
  ];
}

Widget buildNetworkPagesWidget(BuildContext context) =>
    buildListBody("网络", context, _buildRoutes());