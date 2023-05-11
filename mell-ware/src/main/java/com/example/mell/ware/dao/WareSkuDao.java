package com.example.mell.ware.dao;

import com.example.mell.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 * 
 * @author xiaoxing
 * @email xiaoxing@gmail.com
 * @date 2023-04-12 20:43:35
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    /**
     * @param skuId
     * @param wareId
     * @param skuNum
     */
    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);
}
