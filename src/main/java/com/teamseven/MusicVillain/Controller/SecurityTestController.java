package com.teamseven.MusicVillain.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SecurityTestController {

    @GetMapping("/user")
    @ResponseBody
    public String user(){
        // user, manager, admin 권한만 접근 가능
        return "user";
    }
    @GetMapping("/manager")
    @ResponseBody
    public String manager(){
        // manager, admin 권한만 접근 가능
        return "manager";
    }
    @GetMapping("/admin")
    @ResponseBody
    public String admin(){
        // admin 권한만 접근 가능
        return "admin";
    }



}
