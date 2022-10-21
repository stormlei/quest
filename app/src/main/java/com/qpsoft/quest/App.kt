package com.qpsoft.quest

import android.app.Application
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        LogUtils.getConfig().isLogSwitch = false
    }
}