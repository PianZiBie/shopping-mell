package com.example.mell.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mell.ware.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author xiaoxing
 * @email xiaoxing@gmail.com
 * @date 2023-03-26 20:05:50
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

