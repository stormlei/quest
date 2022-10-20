package com.qpsoft.quest.server;

import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.util.concurrent.TimeUnit;

public class ServerManager {

    private Server mServer;

    /**
     * Create server.
     */
    public ServerManager(Context context) {
        mServer = AndServer.webServer(context)
                .port(8080)
                .timeout(10, TimeUnit.SECONDS)
                .listener(new Server.ServerListener() {
                    @Override
                    public void onStarted() {
                        // The server started successfully.
                        LogUtils.e("The server has started successfully.");
                        LogUtils.e(NetworkUtils.getIPAddress(true));
                        //LogUtils.e(NetUtils.getLocalIPAddress().getHostAddress());
                    }

                    @Override
                    public void onStopped() {
                        // The server has stopped.
                        LogUtils.e("The server has stopped.");
                    }

                    @Override
                    public void onException(Exception e) {
                        // An exception occurred while the server was starting.
                        LogUtils.e("The server " + e.getMessage());
                    }
                })
                .build();
    }

    /**
     * Start server.
     */
    public void startServer() {
        if (mServer.isRunning()) {
            // The server is already up.
        } else {
            mServer.startup();
        }
    }

    /**
     * Stop server.
     */
    public void stopServer() {
        if (mServer.isRunning()) {
            mServer.shutdown();
        } else {
            Log.w("AndServer", "The server has not started yet.");
        }
    }
}
