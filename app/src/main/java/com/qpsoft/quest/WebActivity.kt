package com.qpsoft.quest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import com.blankj.utilcode.util.CacheDiskStaticUtils
import com.blankj.utilcode.util.LogUtils
import com.just.agentweb.AgentWeb
import com.qpsoft.quest.config.Const
import com.qpsoft.quest.config.Keys
import com.qpsoft.quest.server.ServerManager

class WebActivity : AppCompatActivity() {

    private lateinit var serverManager: ServerManager
    private val host = "http://127.0.0.1:8080"

    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        val questId = intent.getStringExtra("questId")
        val questType = CacheDiskStaticUtils.getString(Keys.QUEST_TYPE, "")
        val txt = when(questType) {
            "primary" -> {
                url = "$host/3031?type=3031&schoolCategory=PrimarySchool&schoolYearStart=2022&from=student&questionnaire=1&questId=$questId"
                Const.X
            }
            "middle" -> {
                url = "$host/3031?type=3031&schoolCategory=MiddleSchool&schoolYearStart=2022&from=student&questionnaire=1&questId=$questId"
                Const.Z
            }
            "university" -> {
                url = "$host/3031?type=3031&schoolCategory=University&schoolYearStart=2022&from=student&questionnaire=1&questId=$questId"
                Const.D
            }
            else -> ""
        }
        findViewById<TextView>(R.id.tv_top_title).text = txt
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }

        serverManager = ServerManager(this)
        serverManager.startServer()

        LogUtils.e("-----$url")

        AgentWeb.with(this)
            .setAgentWebParent(findViewById(R.id.container), LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator(R.color.color_cb7)
            .createAgentWeb()
            .ready()
            .go(url)
    }

    override fun onDestroy() {
        super.onDestroy()
        serverManager.stopServer()
    }
}