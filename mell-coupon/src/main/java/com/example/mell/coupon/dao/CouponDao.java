package com.example.mell.coupon.dao;

import com.example.mell.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author xiaoxing
 * @email xiaoxing@gmail.com
 * @date 2023-03-26 19:48:37
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
