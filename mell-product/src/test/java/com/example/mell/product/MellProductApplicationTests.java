package com.example.mell.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mell.product.entity.BrandEntity;
import com.example.mell.product.service.BrandService;
import net.minidev.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MellProductApplicationTests {
    @Autowired
    BrandService brandService;
    @Test
    void contextLoads() {
//        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setName("华为有为");
//        brandService.save(brandEntity);
//        System.out.println("测试成功");
        List<BrandEntity> brandId = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 9L));
        brandId.forEach(item->{
                    System.out.println(item);
                });

    }

}
