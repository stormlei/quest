package com.qpsoft.quest.server;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.CacheDiskStaticUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.GsonUtils;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api")
class ApiController {

    @PostMapping("/save")
    public String savaData(Context context, @RequestBody String str) throws JSONException {
        LogUtils.e("------"+str);
        JSONObject jsonObject = new JSONObject(str);
        String uniqueID = UUID.randomUUID().toString();
        String questId = jsonObject.optString("questId");
        if (!"null".equals(questId)) uniqueID = questId;

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("id", uniqueID);
        jsonObj.put("data",jsonObject.getJSONArray("data"));
        jsonObj.put("tabletNo", CacheDiskStaticUtils.getString(Keys.TABLET_NO, ""));
        jsonObj.put("title", getTitle());
        jsonObj.put("time", getTime());
        jsonObj.put("fillTime", jsonObject.optString("fillTime"));
        CacheDiskStaticUtils.put(uniqueID, jsonObj);


        JSONArray jsonArray = CacheDiskStaticUtils.getJSONArray(Keys.QUEST_LIST);
        if (jsonArray == null) jsonArray = new JSONArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            if (jo.getString("id").equals(jsonObj.getString("id"))) {
                jsonArray.remove(i);
            }
        }
        jsonArray.put(jsonObj);
        CacheDiskStaticUtils.put(Keys.QUEST_LIST, jsonArray);


        context.startActivity(new Intent(context, QrCodeActivity.class).putExtra("questId", uniqueID));

        return GsonUtils.toJson(new Result(0, uniqueID, ""));
    }

    @GetMapping(value = "/qrcode")
    public FileBody getQrCode(@RequestParam("id") String id, Context context) {
        JSONObject jsonObj = CacheDiskStaticUtils.getJSONObject(id);
        if (jsonObj == null) return null;
        Bitmap qrCodeBitMap = CodeUtils.createQRCode(EncodeUtils.base64Encode2String(GzipUtil.INSTANCE.gzip(jsonObj.toString())), 720, null);
        File file = persistImage(qrCodeBitMap, context);
        return new FileBody(file);
    }

    private static File persistImage(Bitmap bitmap, Context context) {
        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, "quest.jpg");
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("--------", "Error writing bitmap", e);
        }
        return imageFile;
    }

    @GetMapping("/quest")
    public String getData(@RequestParam("id") String id) {
        JSONObject jsonObj = CacheDiskStaticUtils.getJSONObject(id);
        if (jsonObj == null) return GsonUtils.toJson(new Result(0, null, ""));
        JSONObject jo = new JSONObject();
        try {
            jo.put("code", 0);
            jo.put("data", jsonObj.getJSONArray("data"));
            jo.put("message", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo.toString();
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