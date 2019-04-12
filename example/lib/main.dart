import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_broadcast/flutter_broadcast.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
   static final String HEAD = "android.intent.action";
   static final String ACTION_OPEN_LOCK = HEAD + ".expressopendoor";
   static final String ACTION_OPEN_LOCK_RESULT = HEAD + ".unlockingcontent";
   static final String ACTION_QUERY_LOCK_STATE = HEAD + ".expressopendoorstate";
   static final String ACTION_QUERY_LOCK_STATE_RESULT = HEAD + ".expressopendoorstateresult";
   static final String ACTION_QUERY_IR_STATE = HEAD + ".queryInfrared";
   static final String ACTION_QUERY_IR_STATE_RESULT = HEAD + ".queryInfraredresult";
   static final String ACTION_QUERY_LOCK_IR = HEAD + ".queryLockInfrared";
   static final String ACTION_QUERY_LOCK_IR_RESULT = HEAD + ".queryLockInfraredresult";
   static final String ACTION_MODIFY_BOAR_ID = HEAD + ".settingboarsid";

   static final String PARAM_CONTENT_RESULT = "contentResult";
   static final String PARAM_BOARD_ID = "boardid";
   static final String PARAM_BOARD_IP = "boardip";
   static final String PARAM_LOCK_ID = "lockid";
   static final String PARAM_LOCK_STATE = "lockstate";
   static final String PARAM_IR_STATE = "irstate";
   static final String PARAM_PAGE_SIGN = "pagesign";

   static final String ACTION_RFID_STATT_READ = HEAD + ".rfid_start_read";
   static final String ACTION_RFID_READ_RESULT = HEAD + ".rfid_read_result";
   static final String ACTION_RFID_STOP_READ = HEAD + ".rfid_stop_read";
   static final String PARAM_BLOCK_ADDR = "block_addr";
   static final String PARAM_CARD_NO = "card_no";
   static final String PARAM_BLOCK_DATA = "block_data";

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await FlutterBroadcast.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  var text="test";
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Row(children: <Widget>[
          Text(text),
          RaisedButton(onPressed: () async {
            FlutterBroadcast.sendBroadcast({"action":ACTION_RFID_STATT_READ});
            Map resutl = await FlutterBroadcast.registerBroadcast(ACTION_RFID_READ_RESULT);
            resutl.forEach((k,v){
              print("key=== $k");
              print("value=== $v");
            });

          },)
        ],),
      ),
    );
  }
}
