package com.htc.remedy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Created by poovarasanv on 12/9/17.
 * Project : remedy-web-services
 */
@Controller
@CrossOrigin
@RequestMapping(path = "/query")
public class QueryController {

    @RequestMapping(method = RequestMethod.GET, path = "")
    public String index() {


        return "query/index";
    }
}
