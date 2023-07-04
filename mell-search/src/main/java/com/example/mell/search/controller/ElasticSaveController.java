package com.example.mell.search.controller;

import com.example.common.exception.BizCodeEnum;
import com.example.common.to.es.SkuEsModel;
import com.example.common.utils.R;
import com.example.mell.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
@Slf4j
@RequestMapping("/search/save")
@RestController
public class ElasticSaveController {
    @Resource
    ProductSaveService productSaveService;
    @PostMapping("/product")
    public R productStatsUP(@RequestBody List<SkuEsModel> skuEsModels) {
        boolean b =false;
        try {
           b = productSaveService.productStatsUP(skuEsModels);
        } catch (IOException e) {
            log.error("es商品上架失败: {}",e);
            return R.error(BizCodeEnum.PRODUCT_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_EXCEPTION.getMsg());
        }
        if (b){
            return R.error(BizCodeEnum.PRODUCT_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_EXCEPTION.getMsg());
        }else {
            return R.ok();
        }
    }

}
