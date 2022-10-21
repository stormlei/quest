package com.qpsoft.quest.server;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.blankj.utilcode.util.CacheDiskStaticUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.king.zxing.util.CodeUtils;
import com.qpsoft.quest.QrCodeActivity;
import com.qpsoft.quest.config.Const;
import com.qpsoft.quest.config.Keys;
import com.qpsoft.quest.entity.Result;
import com.qpsoft.quest.util.GzipUtil;
import com.yanzhenjie.andserver.annotation.*;
import com.yanzhenjie.andserver.framework.body.FileBody;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.util.IOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api")
class ApiController {

    @PostMapping("/save")
    public String savaData(Context context, @RequestBody String str) {
        String uniqueID = UUID.randomUUID().toString();
        LogUtils.e("------"+str);
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("id", uniqueID);
            jsonObj.put("data", str);
            jsonObj.put("tabletNo", CacheDiskStaticUtils.getString(Keys.TABLET_NO, ""));
            jsonObj.put("title", getTitle());
            jsonObj.put("time", getTime());
            CacheDiskStaticUtils.put(uniqueID, jsonObj);

            JSONArray jsonArray = CacheDiskStaticUtils.getJSONArray(Keys.QUEST_LIST);
            if (jsonArray == null) jsonArray = new JSONArray();
            jsonArray.put(jsonObj);
            CacheDiskStaticUtils.put(Keys.QUEST_LIST, jsonArray);

            context.startActivity(new Intent(context, QrCodeActivity.class).putExtra("questId", uniqueID));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return GsonUtils.toJson(new Result(0, uniqueID, ""));
    }

    @GetMapping("/qrcode")
    public void getQrCode(@RequestParam("id") String id, HttpResponse response) {
        JSONObject jsonObj = CacheDiskStaticUtils.getJSONObject(id);
        Bitmap qrCodeBitMap = CodeUtils.createQRCode(EncodeUtils.base64Encode2String(GzipUtil.INSTANCE.gzip(jsonObj.toString())), 720, null);
        //File file = FileUtils.
        //com.yanzhenjie.andserver.http.RequestBody body = new FileBody(file);
        //response.setBody(body);
    }

    @GetMapping("/quest")
    public String getData(@RequestParam("id") String id) {
        String data = "";
        JSONObject jsonObj = CacheDiskStaticUtils.getJSONObject(id);
        try {
            if (jsonObj != null) data = jsonObj.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return GsonUtils.toJson(new Result(0, data, ""));
    }

    private String getTitle(){
        String questType = CacheDiskStaticUtils.getString(Keys.QUEST_TYPE, "");
        if ("primary".equals(questType)) {
            return Const.X;
        } else if ("middle".equals(questType)) {
            return Const.Z;
        } else if ("university".equals(questType)) {
            return Const.D;
        }
        return "";
    }

    private String getTime(){
        return TimeUtils.date2String(new Date(), "yyyy年MM月dd日HH时mm分ss秒");
    }
}