package com.qpsoft.quest.server;

import com.blankj.utilcode.util.CacheDiskStaticUtils;
import com.blankj.utilcode.util.LogUtils;
import com.qpsoft.quest.config.Keys;
import com.yanzhenjie.andserver.annotation.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

@RestController
@RequestMapping("/api")
class ApiController {

    private JSONArray jsonArray = new JSONArray();

    @PostMapping("/save")
    public String savaData(@RequestBody String str) {
        try {
            LogUtils.e("------"+str);
            String uniqueID = UUID.randomUUID().toString();
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("uuid", uniqueID);
            jsonObj.put("data", str);
            jsonArray.put(jsonObj);
            CacheDiskStaticUtils.put(Keys.QRCODE_CONTENT, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return "ok";
    }
}