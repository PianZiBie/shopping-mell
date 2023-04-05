package com.example.mell.product.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.example.common.valid.AddGroup;
import com.example.common.valid.UpdateGroup;
import com.example.common.valid.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.mell.product.entity.BrandEntity;
import com.example.mell.product.service.BrandService;

import javax.validation.Valid;


/**
 * 品牌
 *
 * @author xiaoxing
 * @email xiaoxing@gmail.com
 * @date 2023-03-26 17:17:04
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
        BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:brand:save")
    public R save( @Validated({AddGroup.class}) @RequestBody  BrandEntity brand /*, BindingResult */){
//        if (result.hasErrors()){
//            HashMap<String, String> msg = new HashMap<>();
//            result.getFieldErrors().forEach(item ->{
//                String defaultMessage = item.getDefaultMessage();
//                String name = item.getField();
//                msg.put(name,defaultMessage);
//            });
//            return R.error(400,"数据不合法：").put("data",msg);
//        }
//        else{
//                brandService.save(brand);
//         }
        brandService.save(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:brand:update")
    public R update(@Validated(UpdateGroup.class)@RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 修改状态
     * @param brand
     * @return
     */
    @RequestMapping("/update/status")
    //@RequiresPermissions("product:brand:update")
    public R updateStatus(@Validated(UpdateStatusGroup.class)@RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
        brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
