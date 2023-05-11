package com.example.mell.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mell.ware.Vo.MergeVo;
import com.example.mell.ware.Vo.PurchaseDoneVo;
import com.example.mell.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author xiaoxing
 * @email xiaoxing@gmail.com
 * @date 2023-04-12 20:43:36
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void done(PurchaseDoneVo doneVo);

    void received(List<Long> ids);

    void mergePurchase(MergeVo mergeVo);

    PageUtils queryPageUnreceivePurchase(Map<String, Object> params);
}

