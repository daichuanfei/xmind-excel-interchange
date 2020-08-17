package com.lab.xmind.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DateFormat;
import java.util.Date;

/**
 * @Description TODO
 * @Author
 * @Date 2020/7/21 下午4:05
 **/

/**
 * 注意：spring.mvc.view.prefix=/WEB-INF/views/，最后的/不能丢
 * spring.mvc.view.suffix=.jsp
 */

@Controller
public class DemoController {


    @RequestMapping("/demo")
    public String demo(Model model) {
        model.addAttribute("now", DateFormat.getDateTimeInstance().format(new Date()));
        return "demo";
    }
}
