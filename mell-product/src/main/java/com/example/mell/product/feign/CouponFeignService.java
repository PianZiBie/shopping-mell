package com.example.mell.product.feign;

import com.example.common.to.SkuReductionTo;
import com.example.common.to.SpuBoundTo;
import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("mell-coupon")
public interface CouponFeignService {
    @PostMapping("coupon/spubounds/save")
     R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);
     @PostMapping("coupon/skufullreduction/saveInfo")
     R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
