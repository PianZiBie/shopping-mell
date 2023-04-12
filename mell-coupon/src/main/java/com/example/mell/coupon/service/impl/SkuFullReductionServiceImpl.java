package com.example.mell.coupon.service.impl;

import com.example.common.to.MemberPrice;
import com.example.common.to.SkuReductionTo;
import com.example.mell.coupon.entity.MemberPriceEntity;
import com.example.mell.coupon.entity.SkuLadderEntity;
import com.example.mell.coupon.service.MemberPriceService;
import com.example.mell.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mell.coupon.dao.SkuFullReductionDao;
import com.example.mell.coupon.entity.SkuFullReductionEntity;
import com.example.mell.coupon.service.SkuFullReductionService;

import javax.annotation.Resource;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {
    @Resource
    SkuLadderService skuLadderService;
    @Resource
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {

        //1保存sku优惠满减
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
        skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
        skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
//        skuLadderEntity.setPrice();
        if (skuReductionTo.getFullCount() > 0) {
            skuLadderService.save(skuLadderEntity);
        }
        //2sms-sku-full-reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
        if (skuReductionTo.getFullPrice().compareTo(new BigDecimal("0"))>0) {
            this.save(skuFullReductionEntity);
        }
        //3sms_member_price
        List<MemberPrice> memberPrices = skuReductionTo.getMemberPrice();
        if (memberPrices != null && memberPrices.size() > 0) {
            List<MemberPriceEntity> collect = memberPrices.stream().map(
                    item -> {
                        MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                        memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
                        memberPriceEntity.setMemberLevelId(item.getId());
                        memberPriceEntity.setMemberLevelName((item.getName()));
                        memberPriceEntity.setMemberPrice(item.getPrice());
                        memberPriceEntity.setAddOther(1);
                        return memberPriceEntity;
                    }
            ).filter(item->{
                return item.getMemberPrice().compareTo(new BigDecimal("0"))>0;
            }).collect(Collectors.toList());
            memberPriceService.saveBatch(collect);
        }

    }

}