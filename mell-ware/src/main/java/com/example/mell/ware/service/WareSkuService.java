package com.example.mell.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mell.ware.entity.WareSkuEntity;

import java.util.Map;

/**
 * 商品库存
 *
 * @author xiaoxing
 * @email xiaoxing@gmail.com
 * @date 2023-04-12 20:43:35
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);
}

