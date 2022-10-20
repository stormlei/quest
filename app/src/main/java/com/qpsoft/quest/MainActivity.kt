package com.qpsoft.quest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.utilcode.util.CacheDiskStaticUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.qpsoft.quest.config.Const
import com.qpsoft.quest.config.Keys
import com.qpsoft.quest.server.ServerManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.tv_open_quest).setOnClickListener {
            val questType = CacheDiskStaticUtils.getString(Keys.QUEST_TYPE, "")
            if (!TextUtils.isEmpty(questType)) {
                startActivity(Intent(this@MainActivity, WebActivity::class.java))
            } else {
                ToastUtils.showShort("暂未设置调查表,请到调查表管理进行设置")
            }
        }

        findViewById<LinearLayout>(R.id.ll_setting).setOnClickListener {
            startActivity(Intent(this@MainActivity, PassWordActivity::class.java))
        }

    }

    override fun onResume() {
        super.onResume()

        val questType = CacheDiskStaticUtils.getString(Keys.QUEST_TYPE, "")
        val txt = when(questType) {
            "primary" -> Const.X
            "middle" -> Const.Z
            "university" -> Const.D
            else -> "暂未设置调查表,请到调查表管理进行设置"
        }
        findViewById<TextView>(R.id.tv_quest_type).text = txt
    }
}