package com.example.mell.product.feign;

import com.example.common.to.es.SkuEsModel;
import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("mell-search")
public interface SearchFeignServer {
    @PostMapping("/search/save/product")
    public R productStatsUP(@RequestBody List<SkuEsModel> skuEsModels);
}
