package com.qpsoft.quest.autoupdate

import android.app.Activity
import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.qpsoft.quest.entity.AppVersion

/**
 * Created by stormlei on 2017/4/6.
 */
class UpdateManager(context: Activity, appVersion: AppVersion) {
    private val mContext: Context
    private val appVersion: AppVersion

    private lateinit var title: String
    private lateinit var downloadUrl: String
    private lateinit var versionInfo: String

    init {
        this.mContext = context
        this.appVersion = appVersion
    }

    /**
     * 检测更新
     */
    fun checkUpdate() {
        val force = appVersion.forceUpdate
        title = "更新"
        downloadUrl = appVersion.downloadUrl
        versionInfo = appVersion.updateContent
        if (!force) {
            showNoticeDialog()
        } else {
            showNoticeDialogForce()
        }
    }

    /**
     * 显示更新对话框
     *
     *
     */
    private fun showNoticeDialog() {
        MaterialDialog(mContext).show {
            title(text = title)
            message(text = versionInfo)
            cornerRadius(16f)
            negativeButton {  }
            positiveButton(text="后台更新") {
                DownloadUtil(mContext, downloadUrl).loadFile()
            }
        }
    }

    private fun showNoticeDialogForce() {
        MaterialDialog(mContext).cancelable(true).show {
            title(text = title)
            message(text = versionInfo)
            cornerRadius(16f)
            cancelOnTouchOutside(true)
            positiveButton(text="后台更新") {
                DownloadUtil(mContext, downloadUrl).loadFile()
            }
        }
    }
}