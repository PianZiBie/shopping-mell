package com.example.mell.search.controller;


import com.example.mell.search.service.MallSearchService;
import com.example.mell.search.vo.SearchParam;
import com.example.mell.search.vo.SearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;



@Controller
public class SearchController {

    @Resource
    MallSearchService mallSearchService;

    @GetMapping(value = "/list.html")
    public String listPage(SearchParam param, Model model, HttpServletRequest request) {

        param.set_queryString(request.getQueryString());

        //1、根据传递来的页面的查询参数，去es中检索商品
        SearchResult search = mallSearchService.search(param);

        model.addAttribute("result",search);

        return "list";
    }

}
