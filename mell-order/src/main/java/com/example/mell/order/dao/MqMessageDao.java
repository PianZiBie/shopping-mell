package com.example.mell.order.dao;

import com.example.mell.order.entity.MqMessageEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author xiaoxing
 * @email xiaoxing@gmail.com
 * @date 2023-03-26 19:40:08
 */
@Mapper
public interface MqMessageDao extends BaseMapper<MqMessageEntity> {
	
}
