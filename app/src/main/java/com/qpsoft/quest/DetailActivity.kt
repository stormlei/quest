package com.qpsoft.quest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.blankj.utilcode.util.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.king.zxing.util.CodeUtils
import com.qpsoft.quest.config.Keys
import com.qpsoft.quest.entity.Quest
import com.qpsoft.quest.util.GzipUtil.gzip
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import java.util.zip.GZIPOutputStream

class DetailActivity : AppCompatActivity() {

    private lateinit var mAdapter: BaseQuickAdapter<Quest, BaseViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        findViewById<TextView>(R.id.tv_top_title).text = "历史填单"
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }

        val data = mutableListOf<Quest>()
        val jsonArray = CacheDiskStaticUtils.getJSONArray(Keys.QUEST_LIST)
        if (jsonArray != null) {
            val objList = jsonArray.toMutableList().reversed()
            for (obj in objList) {
                data.add(GsonUtils.fromJson(obj.toString(), Quest::class.java))
            }
        }

        val historyRv = findViewById<RecyclerView>(R.id.rv_history)
        historyRv.layoutManager = LinearLayoutManager(this)
        mAdapter = object: BaseQuickAdapter<Quest, BaseViewHolder>(R.layout.item_history, data) {
            override fun convert(holder: BaseViewHolder, item: Quest) {
                holder.setText(R.id.tv_title, item.title)
                holder.setText(R.id.tv_time, item.time+"提交")
            }

        }
        historyRv.adapter = mAdapter

        mAdapter.addChildClickViewIds(R.id.iv_edit, R.id.iv_qrcode)
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            val quest = mAdapter.getItem(position)
            when(view.id) {
                R.id.iv_edit -> {
                    showEdit(quest)
                }
                R.id.iv_qrcode -> {
                    showQrCode(quest)
                }
            }
        }
    }

    private fun JSONArray.toMutableList(): MutableList<JSONObject> = MutableList(length(), this::getJSONObject)

    private fun showQrCode(quest: Quest) {
        val editDialog = MaterialDialog(this).show {
            customView(R.layout.dialog_qrcode, scrollable = true, noVerticalPadding = false)
            cornerRadius(16f)
            lifecycleOwner(this@DetailActivity)
        }
        val rootView = editDialog.getCustomView()
        rootView.findViewById<ImageView>(R.id.iv_close).setOnClickListener {
            editDialog.dismiss()
        }
        val qrCodeIv = rootView.findViewById<ImageView>(R.id.iv_qrcode)
        LogUtils.e("--------$quest")
        //val qrCodeBitMap = CodeUtils.createQRCode(GsonUtils.toJson(quest.toString()), 1000, null)
        val qrCodeBitMap = CodeUtils.createQRCode(EncodeUtils.base64Encode2String(gzip(quest.toString())), 720, null)
        qrCodeIv.setImageBitmap(qrCodeBitMap)

        val txt1Tv = rootView.findViewById<TextView>(R.id.tv_txt1)
        val txt2Tv = rootView.findViewById<TextView>(R.id.tv_txt2)
        txt1Tv.text = quest.title
        txt2Tv.text = quest.time+"填写"
    }

    private fun showEdit(quest: Quest) {
        val editDialog = MaterialDialog(this).show {
            customView(R.layout.dialog_edit, scrollable = true, noVerticalPadding = false)
            cornerRadius(16f)
            lifecycleOwner(this@DetailActivity)
        }
        val rootView = editDialog.getCustomView()
        rootView.findViewById<ImageView>(R.id.iv_close).setOnClickListener {
            editDialog.dismiss()
        }
        rootView.findViewById<TextView>(R.id.tv_ok).setOnClickListener {
            editDialog.dismiss()
            startActivity(Intent(this@DetailActivity, WebActivity::class.java).putExtra("questId", quest.id))
        }
    }
}