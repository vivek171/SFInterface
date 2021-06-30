package com.htc.remedy.controller;

import com.htc.remedy.base.SFInterfaceBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;


@Controller
@CrossOrigin
public class IndexController {
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(HttpServletRequest request) throws Exception {
        return SFInterfaceBase.validateAndreturnPage("index", request);
    }
}
