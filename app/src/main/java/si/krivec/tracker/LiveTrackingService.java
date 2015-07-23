package si.krivec.tracker;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import java.util.concurrent.ExecutionException;

import asynctasks.GetUserLiveLocation;
import utils.Constants;
import utils.TrackerPoint;

/**
 * Created by Luka on 23.7.2015.
 */
public class LiveTrackingService extends Service {

    public static long lastTimestamp;
    public static TrackerPoint[] livePoints;

    private final int MSG_UPDATE_POINTS = 1;
    private Handler mHandler;
    private int onlineRouteId;

    @Override
    public void onCreate() {
         mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_UPDATE_POINTS:
                        try {
                            livePoints = new GetUserLiveLocation().execute(onlineRouteId+"", lastTimestamp+"").get();
                            if(livePoints != null && livePoints.length > 0) {
                                lastTimestamp = livePoints[livePoints.length-1].getTime();
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_POINTS, Constants.LIVE_TRACKING_UPDATE_INTERVAL*1000);
                        break;
                }
            }
        };
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onlineRouteId = intent.getExtras().getInt("onlineRouteId");
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_POINTS, Constants.LIVE_TRACKING_UPDATE_INTERVAL*1000);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mHandler.removeMessages(MSG_UPDATE_POINTS);
    }
}
