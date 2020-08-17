package com.lab.xmind.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description TODO
 * @Author
 * @Date 2020/8/17 下午5:35
 **/
@Controller
@RequestMapping("/")
public class IndexController {
    public String index(Model model) {
        return "index";
    }
}
