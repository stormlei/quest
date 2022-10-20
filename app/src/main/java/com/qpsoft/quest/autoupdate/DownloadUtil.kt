package com.qpsoft.quest.autoupdate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.core.app.NotificationCompat
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend.Listener4SpeedModel
import com.liulishuo.okdownload.SpeedCalculator
import com.liulishuo.okdownload.core.breakpoint.BlockInfo
import com.liulishuo.okdownload.core.cause.EndCause
import com.blankj.utilcode.util.AppUtils
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.qpsoft.quest.R
import java.lang.Exception

/**
 * Created by stormlei on 2017/4/6.
 */
class DownloadUtil(private val mContext: Context, private val downloadUrl: String) {
    /**
     * 目标文件存储的文件名
     */
    private val destFileName = "quest.apk"
    private val destFile = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absoluteFile

    /**
     * 下载文件
     */
    fun loadFile() {
        val task = DownloadTask.Builder(downloadUrl, destFile)
            .setFilename(destFileName) // the minimal interval millisecond for callback progress
            .setMinIntervalMillisCallbackProcess(30) // do re-download even if the task has already been completed in the past.
            .setPassIfAlreadyCompleted(false)
            .setConnectionCount(1)
            .build()
        task.enqueue(object : DownloadListener4WithSpeed() {
            private var totalLength: Long = 0
            override fun taskStart(task: DownloadTask) {
                initNotification()
            }

            override fun infoReady(task: DownloadTask, info: BreakpointInfo, fromBreakpoint: Boolean, model: Listener4SpeedModel) {
                totalLength = info.totalLength
            }

            override fun connectStart(task: DownloadTask, blockIndex: Int, requestHeaderFields: Map<String, List<String>>) {
            }

            override fun connectEnd(task: DownloadTask, blockIndex: Int, responseCode: Int, responseHeaderFields: Map<String, List<String>>) {
            }

            override fun progressBlock(task: DownloadTask, blockIndex: Int, currentBlockOffset: Long, blockSpeed: SpeedCalculator) {
            }

            override fun progress(task: DownloadTask, currentOffset: Long, taskSpeed: SpeedCalculator) {
                val percent = currentOffset.toFloat() / totalLength * 100
                updateNotification(percent.toLong())
            }

            override fun blockEnd(task: DownloadTask, blockIndex: Int, info: BlockInfo, blockSpeed: SpeedCalculator) {
            }

            override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: Exception?, taskSpeed: SpeedCalculator) {
                cancelNotification()
                if (cause == EndCause.COMPLETED) AppUtils.installApp(task.file)
            }
        })

//        OkGo.<File>get(downloadUrl)
//            .execute(new FileCallback(destFileDir, destFileName) {
//                @Override
//                public void onSuccess(Response<File> response) {
//                    cancelNotification();
//                    stopSelf();
//                    AppUtils.installApp(response.body());
//                }
//
//                @Override
//                public void onError(Response<File> response) {
//                    super.onError(response);
//                    ToastUtils.showShort("下载失败");
//                    cancelNotification();
//                    stopSelf();
//                }
//
//                @Override
//                public void downloadProgress(Progress progress) {
//                    super.downloadProgress(progress);
//                    updateNotification((long) (progress.fraction * 100));
//                }
//            });
    }

    private var preProgress = 0
    private val NOTIFY_ID = 1000
    private var builder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null

    /**
     * 初始化Notification通知
     */
    fun initNotification() {
        notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        builder = NotificationCompat.Builder(mContext, createNotificationChannel())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText("0%")
            .setContentTitle(mContext.resources.getString(R.string.app_name))
            .setProgress(100, 0, false)
        notificationManager!!.notify(NOTIFY_ID, builder!!.build())
    }

    /**
     * 更新通知
     */
    fun updateNotification(progress: Long) {
        val currProgress = progress.toInt()
        if (preProgress < currProgress) {
            builder!!.setContentText("$progress%")
            builder!!.setProgress(100, progress.toInt(), false)
            notificationManager!!.notify(NOTIFY_ID, builder!!.build())
        }
        preProgress = progress.toInt()
    }

    /**
     * 取消通知
     */
    fun cancelNotification() {
        if (notificationManager != null) notificationManager!!.cancel(NOTIFY_ID)
    }

    private fun createNotificationChannel(): String {
        // NotificationChannels are required for Notifications on O (API 26) and above.
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Initializes NotificationChannel.
            val channelId = AppUtils.getAppName()
            val notificationChannel = NotificationChannel(
                channelId,
                channelId,
                NotificationManager.IMPORTANCE_MIN
            )
            notificationChannel.description = ""
            notificationChannel.enableVibration(false)
            notificationManager!!.createNotificationChannel(notificationChannel)
            channelId
        } else {
            // Returns null for pre-O (26) devices.
            AppUtils.getAppName()
        }
    }
}