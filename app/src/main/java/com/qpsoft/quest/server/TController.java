package com.qpsoft.quest.server;

import com.yanzhenjie.andserver.annotation.Controller;
import com.yanzhenjie.andserver.annotation.GetMapping;


@Controller
class TController {
    @GetMapping("/3031")
    public String index() {
        return "forward:/index.html";
    }
}