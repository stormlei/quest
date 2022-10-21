package com.qpsoft.quest

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.CacheDiskStaticUtils
import com.blankj.utilcode.util.EncodeUtils
import com.king.zxing.util.CodeUtils
import com.qpsoft.quest.config.Const
import com.qpsoft.quest.config.Keys
import com.qpsoft.quest.util.GzipUtil

class QrCodeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)

        val questType = CacheDiskStaticUtils.getString(Keys.QUEST_TYPE, "")
        val txt = when(questType) {
            "primary" -> Const.X
            "middle" -> Const.Z
            "university" -> Const.D
            else -> ""
        }
        findViewById<TextView>(R.id.tv_top_title).text = txt
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }

        val questId = intent.getStringExtra("questId")!!
        val jsonObj = CacheDiskStaticUtils.getJSONObject(questId)
        val qrCodeIv = findViewById<ImageView>(R.id.iv_qrcode)
        val qrCodeBitMap = CodeUtils.createQRCode(EncodeUtils.base64Encode2String(GzipUtil.gzip(jsonObj.toString())), 720, null)
        qrCodeIv.setImageBitmap(qrCodeBitMap)

        findViewById<TextView>(R.id.tv_go_main).setOnClickListener {
            startActivity(Intent(this@QrCodeActivity, MainActivity::class.java))
        }

        findViewById<TextView>(R.id.tv_go_edit).setOnClickListener {
            finish()
            //startActivity(Intent(this@QrCodeActivity, WebActivity::class.java))
        }
    }
}