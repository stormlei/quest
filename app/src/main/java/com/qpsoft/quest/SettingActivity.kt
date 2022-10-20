package com.qpsoft.quest

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.blankj.utilcode.util.*
import com.qpsoft.quest.autoupdate.UpdateManager
import com.qpsoft.quest.config.Keys
import com.qpsoft.quest.entity.AppVersion
import com.qpsoft.quest.entity.Result
import java.io.IOException
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        findViewById<TextView>(R.id.tv_top_title).text = "调查表管理"
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }

        val questType = CacheDiskStaticUtils.getString(Keys.QUEST_TYPE, "")
        when(questType) {
            "primary" -> findViewById<RadioButton>(R.id.rb_x).isChecked = true
            "middle" -> findViewById<RadioButton>(R.id.rb_z).isChecked = true
            "university" -> findViewById<RadioButton>(R.id.rb_d).isChecked = true
        }
        findViewById<RadioGroup>(R.id.rg).setOnCheckedChangeListener { radioGroup, checkId ->
            when(checkId) {
                R.id.rb_x -> CacheDiskStaticUtils.put(Keys.QUEST_TYPE, "primary")
                R.id.rb_z -> CacheDiskStaticUtils.put(Keys.QUEST_TYPE, "middle")
                R.id.rb_d -> CacheDiskStaticUtils.put(Keys.QUEST_TYPE, "university")
            }
        }



        val tabletNoEdt = findViewById<EditText>(R.id.edt_tablet_no)
        val tabletNo = CacheDiskStaticUtils.getString(Keys.TABLET_NO, "")
        tabletNoEdt.setText(tabletNo)
        tabletNoEdt.addTextChangedListener {
            val tableNo = it.toString()
            CacheDiskStaticUtils.put(Keys.TABLET_NO, tableNo)
        }


        findViewById<TextView>(R.id.tv_detail).setOnClickListener {
            startActivity(Intent(this@SettingActivity, DetailActivity::class.java))
        }


        findViewById<TextView>(R.id.tv_app_upgrade).setOnClickListener {
            if (!NetworkUtils.isConnected()) {
                ToastUtils.showShort("请连接网络")
                return@setOnClickListener
            }
            val versionCode = AppUtils.getAppVersionCode()
            val packageName = AppUtils.getAppPackageName()
            Thread {
                val result = run("https://central.qpsc365.com/api/public/v1/app/version/latest?package=$packageName&versionCode=$versionCode")
                LogUtils.e("-----$result")
                runOnUiThread {
                    val result = GsonUtils.fromJson(result, Result::class.java)
                    if (result.code == 0) {
                        val appVersion = result.data
                        UpdateManager(this, appVersion).checkUpdate()
                    }
                }
            }.start()

        }
    }

    @Throws(IOException::class)
    fun run(url: String): String? {
        val request: Request = Request.Builder().url(url).build()
        OkHttpClient().newCall(request).execute().use { response ->
            return response.body?.string()
        }
    }
}