package com.qpsoft.quest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.blankj.utilcode.util.ToastUtils
import com.just.agentweb.AgentWeb
import com.qpsoft.quest.server.ServerManager
import com.qpsoft.quest.view.PwdInputView

class PassWordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        findViewById<TextView>(R.id.tv_top_title).text = "调查表管理"
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }

        findViewById<PwdInputView>(R.id.pwd_input_view).addTextChangedListener {
            if (it.toString().length == 4) {
                if (it.toString() == "8888") {
                    startActivity(Intent(this@PassWordActivity, SettingActivity::class.java))
                    finish()
                } else {
                    ToastUtils.showShort("密码错误！")
                }
            }
        }
    }
}