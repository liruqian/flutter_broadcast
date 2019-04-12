import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_broadcast/flutter_broadcast.dart';

void main() {
  const MethodChannel channel = MethodChannel('flutter_broadcast');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await FlutterBroadcast.platformVersion, '42');
  });
}
