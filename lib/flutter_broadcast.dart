import 'dart:async';

import 'package:flutter/services.dart';

class FlutterBroadcast {
  static const MethodChannel _channel =
      const MethodChannel('flutter_broadcast');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }


  static Future<void> sendBroadcast(Map map) async {
    await _channel.invokeMethod('sendBroadcast',map);
  }

  static Future<Map> registerBroadcast(String action) async {
    Map result = await _channel.invokeMethod('registerBroadcast',{"action":action});
    return result;
  }
}
