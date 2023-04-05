package com.example.mell.order.dao;

import com.example.mell.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author xiaoxing
 * @email xiaoxing@gmail.com
 * @date 2023-03-26 19:40:08
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
