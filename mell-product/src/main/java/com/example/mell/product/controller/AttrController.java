package com.example.mell.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.example.mell.product.entity.ProductAttrValueEntity;
import com.example.mell.product.service.ProductAttrValueService;
import com.example.mell.product.vo.AttrRespVo;
import com.example.mell.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;
import com.example.mell.product.entity.AttrEntity;
import com.example.mell.product.service.AttrService;


/**
 * 商品属性
 *
 * @author xiaoxing
 * @email xiaoxing@gmail.com
 * @date 2023-03-26 17:17:04
 */

@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;

    @GetMapping("/basse/listforspu/{spuId}")
    public R baseAttrListForSpu( @PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> entities=productAttrValueService.baseAttrListForSpu(spuId);
        return R.ok().put("data",entities);
    }

    /**
     * @param params
     * @param catelogId
     * @return
     */
    /**
     * 获取属性关联
     * @param params
     * @param catelogId
     * @param type
     * @return
     */
    @RequestMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId") Long catelogId,
                          @PathVariable("attrType")String type
    ) {
        PageUtils page = attrService.queryBaseAttrPage(params, catelogId,type);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId) {
//		AttrEntity attr = attrService.getById(attrId);
        AttrRespVo respVo = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", respVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr) {
        attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr) {
        attrService.updateAttr(attr);

        return R.ok();
    }
    @PostMapping("/update/{spuId}")
    //@RequiresPermissions("product:attr:update")
    public R updateSpuAttr(@PathVariable("spuId") Long spuId,
                           @RequestBody List<ProductAttrValueEntity> entities) {
        productAttrValueService.updateSpuAttr(spuId,entities);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
