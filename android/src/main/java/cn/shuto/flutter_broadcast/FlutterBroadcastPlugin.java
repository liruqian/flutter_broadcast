package cn.shuto.flutter_broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterBroadcastPlugin
 */
public class FlutterBroadcastPlugin implements MethodCallHandler {
    private static final String HEAD = "android.intent.action";
    public static final String ACTION_OPEN_LOCK = HEAD + ".expressopendoor";
    public static final String ACTION_OPEN_LOCK_RESULT = HEAD + ".unlockingcontent";
    public static final String ACTION_QUERY_LOCK_STATE = HEAD + ".expressopendoorstate";
    public static final String ACTION_QUERY_LOCK_STATE_RESULT = HEAD + ".expressopendoorstateresult";
    public static final String ACTION_QUERY_IR_STATE = HEAD + ".queryInfrared";
    public static final String ACTION_QUERY_IR_STATE_RESULT = HEAD + ".queryInfraredresult";
    public static final String ACTION_QUERY_LOCK_IR = HEAD + ".queryLockInfrared";
    public static final String ACTION_QUERY_LOCK_IR_RESULT = HEAD + ".queryLockInfraredresult";
    public static final String ACTION_MODIFY_BOAR_ID = HEAD + ".settingboarsid";

    public static final String PARAM_CONTENT_RESULT = "contentResult";
    public static final String PARAM_BOARD_ID = "boardid";
    public static final String PARAM_BOARD_IP = "boardip";
    public static final String PARAM_LOCK_ID = "lockid";
    public static final String PARAM_LOCK_STATE = "lockstate";
    public static final String PARAM_IR_STATE = "irstate";
    public static final String PARAM_PAGE_SIGN = "pagesign";


    private Context context;

    FlutterBroadcastPlugin(Context context) {
        this.context = context;
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_broadcast");
        registrar.activeContext().sendBroadcast(new Intent(""));
        channel.setMethodCallHandler(new FlutterBroadcastPlugin(registrar.context()));
        final Registrar registrarEv = registrar;

        new EventChannel(registrar.messenger(), "cn.shutu.broadcast/lock_status").setStreamHandler(
                new EventChannel.StreamHandler() {
                    // 接收广播的BroadcastReceiver
                    private BroadcastReceiver statusReceiver;

                    @Override
                    public void onListen(Object o, EventChannel.EventSink eventSink) {
                        statusReceiver = creatStatusReceiver(eventSink);
                        registrarEv.activeContext().registerReceiver(statusReceiver, new IntentFilter(ACTION_QUERY_LOCK_IR_RESULT));
                    }

                    @Override
                    public void onCancel(Object o) {
                        registrarEv.activeContext().unregisterReceiver(statusReceiver);
                        statusReceiver = null;
                    }
                }
        );
    }

    private static BroadcastReceiver creatStatusReceiver(final EventChannel.EventSink eventSink) {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int result = intent.getIntExtra(PARAM_CONTENT_RESULT, -1);
                int lockBoardId = intent.getIntExtra(PARAM_BOARD_ID, 0);
                String pageSign = intent.getStringExtra(PARAM_PAGE_SIGN);
                //pagesign 的值为 "1234567890"
                if (result == 1) {
                    //正常返回，解析状态
                    boolean[] lockState = intent.getBooleanArrayExtra(PARAM_LOCK_STATE);
                    List<Boolean> list = new ArrayList();
                    for (boolean b : lockState) {
                        list.add(b);
                    }
                    //发送list集合到flutter
                    eventSink.success(list);
                    // lockState[0] 表示锁ID为1的锁是否关闭，true为关闭，false为打开
                } else if (result == 0) {
                    //返回错误，不用处理返回值
                }
            }
        };
    }


    @Override
    public void onMethodCall(MethodCall call, final Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("sendBroadcast")) {
            String action = call.argument("action");
            Intent intent = new Intent(action);
            Map arguments = call.arguments();
            Set<String> set = arguments.keySet();
            for (String key : set) {
                if (!key.equals("action")) {
                    if (arguments.get(key) instanceof Integer) {
                        intent.putExtra(key, (Integer) arguments.get(key));
                    } else if (arguments.get(key) instanceof String) {
                        intent.putExtra(key, (String) arguments.get(key));
                    } else if (arguments.get(key) instanceof Boolean) {
                        intent.putExtra(key, (Boolean) arguments.get(key));
                    } else if (arguments.get(key) instanceof Double) {
                        intent.putExtra(key, (Double) arguments.get(key));
                    }
                }
            }

            context.sendBroadcast(intent);


        } else if (call.method.equals("registerBroadcast")) {
            String action = call.argument("action");
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle extras = intent.getExtras();
                    Set<String> keys = extras.keySet();
                    Map map = new HashMap();
                    for (String key : keys) {
                        //vaule不给空，且value是数组对象，转化成list返回
                        if (null !=extras.get(key) && extras.get(key).getClass().isArray()) {
                            int length = Array.getLength(extras.get(key));
                            Array.get(extras.get(key), 0);
                            List list = new ArrayList();
                            for (int i = 0; i < length; i++) {
                                list.add(Array.get(extras.get(key), i));
                            }
                            map.put(key, list);
                        } else {
                            map.put(key, extras.get(key));
                        }
                    }
                    result.success(map);
                    context.unregisterReceiver(this);
                }
            }, new IntentFilter(action));
        } else {
            result.notImplemented();
        }
    }
}
