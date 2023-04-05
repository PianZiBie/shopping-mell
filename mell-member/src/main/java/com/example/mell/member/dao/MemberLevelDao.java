package com.example.mell.member.dao;

import com.example.mell.member.entity.MemberLevelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级
 * 
 * @author xiaoxing
 * @email xiaoxing@gmail.com
 * @date 2023-03-26 19:58:36
 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {
	
}
