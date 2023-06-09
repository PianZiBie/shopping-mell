package com.example.mell.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.example.common.constant.ProductConstant;
import com.example.common.to.SkuHasStockVo;
import com.example.common.to.SkuReductionTo;
import com.example.common.to.SpuBoundTo;
import com.example.common.to.es.SkuEsModel;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.common.utils.R;
import com.example.mell.product.entity.*;
import com.example.mell.product.feign.CouponFeignService;
import com.example.mell.product.feign.SearchFeignServer;
import com.example.mell.product.feign.WareFeignService;
import com.example.mell.product.service.*;
import com.example.mell.product.vo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.mell.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Resource
    SpuInfoDescService spuInfoDescService;
    @Resource
    SpuImagesService imagesService;
    @Resource
    AttrService attrService;
    @Resource
    ProductAttrValueService productAttrValueService;
    @Resource
    SkuInfoService skuInfoService;
    @Resource
    SkuImagesService skuImagesService;
    @Resource
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Resource
    CouponFeignService couponFeignService;
    @Resource
    BrandService brandService;
    @Resource
    CategoryService categoryService;
    @Resource
    WareFeignService wareFeignService;
    @Resource
    SearchFeignServer searchFeignServer;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }
    @Transactional
    @Override
    public void save(SpuSaveVo vo) {
        //1.保存spu基本信息 pms_spu_info

        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);


        //2.保存spu的描述图片 pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);


        //3.保存spu的图片集  pms_spu_images
        List<String> images = vo.getImages();
        imagesService.saveImages(images,spuInfoEntity.getId());


        //4.保存spu的规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(
                attr -> {
                    ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                    productAttrValueEntity.setAttrId(attr.getAttrId());
                    AttrEntity attrEntity = attrService.getById(attr.getAttrId());
                    productAttrValueEntity.setAttrName(attrEntity.getAttrName());
                    productAttrValueEntity.setAttrValue(attr.getAttrValues());
                    productAttrValueEntity.setQuickShow(attr.getShowDesc());
                    productAttrValueEntity.setSpuId(spuInfoEntity.getId());
                    return productAttrValueEntity;
                }
        ).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);


        //5 保存spu的积分信息 sms_spu_bounds (跨库)
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if (r.getCode()!=0){
            log.error("远程保存spu积分信息失败");
        }


        //5.保存当前spu对应的所有sku信息
            //5.1保存sku基本信息 pms_sku_info
        List<Skus> skus = vo.getSkus();
        if (skus!=null&&skus.size()>0){
        skus.forEach(
                item->{
                    String defaultImg="";
                    for (Images image :item.getImages()){
                        if (image.getDefaultImg() == 1){
                            defaultImg=image.getImgUrl();
                        }
                    }
                    SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                    BeanUtils.copyProperties(item,skuInfoEntity);
                    skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                    skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                    skuInfoEntity.setSaleCount(0L);
                    skuInfoEntity.setSpuId(spuInfoEntity.getId());
                    skuInfoEntity.setSkuDefaultImg(defaultImg);
                    skuInfoService.saveSkuInfo(skuInfoEntity);
                    Long spuId = skuInfoEntity.getSpuId();
                    List<SkuImagesEntity> SkuImagesList = item.getImages().stream().map(
                            img -> {
                                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                                skuImagesEntity.setSkuId(spuId);
                                skuImagesEntity.setImgUrl(img.getImgUrl());
                                skuImagesEntity.setDefaultImg(img.getDefaultImg());
                                return skuImagesEntity;
                            }
                    ).filter(entity->{
                        return StringUtils.isNotEmpty(entity.getImgUrl());
                    }).collect(Collectors.toList());
                    //5.2保存sku图片信息 pms_sku_images
                    skuImagesService.saveBatch(SkuImagesList);
                    //5.3保存sku销售属性信息 pms_sku_sale_attr_value
                    List<Attr> attr = item.getAttr();
                    List<SkuSaleAttrValueEntity> SkuSaleAttrValueEntityList = attr.stream().map(
                            a -> {
                                SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                                BeanUtils.copyProperties(a, skuSaleAttrValueEntity);
                                skuSaleAttrValueEntity.setAttrId(spuId);
                                return skuSaleAttrValueEntity;
                            }
                    ).collect(Collectors.toList());
                    skuSaleAttrValueService.saveBatch(SkuSaleAttrValueEntityList);
                    //5.4保存sku优惠，满减等信息 sms_sku_ladder(跨库)
                    SkuReductionTo skuReductionTo = new SkuReductionTo();
                    BeanUtils.copyProperties(item,skuReductionTo);
                    skuReductionTo.setSkuId(spuId);
                    if (skuReductionTo.getFullCount()>0||skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0){
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode()!=0){
                        log.error("远程保存sku优惠，满减信息失败");
                    }
                }
                }
        );
        }



    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

            QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
            String key = (String) params.get("key");
            if (StringUtils.isNotEmpty(key)){
                queryWrapper.and( item->{
                            item.eq("id",key).or().like("spu_name",key);
                        }
                );
            }
            String status = (String) params.get("status");
            if (StringUtils.isNotEmpty(status)){
                queryWrapper.eq("publish_status",status);
            }
            String brandId = (String) params.get("brandId");
            if (StringUtils.isNotEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
                queryWrapper.eq("brand_id",brandId);
            }
            String catelogId = (String) params.get("catelogId");
            if (StringUtils.isNotEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
                queryWrapper.eq("catalog_id",catelogId);
            }
            IPage<SpuInfoEntity> page = this.page(
                    new Query<SpuInfoEntity>().getPage(params),
                    queryWrapper
            );

            return new PageUtils(page);

    }

    /**
     * 商品上架
     * @param spuId
     */
    @Override
    public void up(Long spuId) {
        List<SkuInfoEntity> skus=skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIdList = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        //TODO 4:查询当前sku的所有可以被用来检索的规格属性
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIds = productAttrValueEntities.stream().map(
                ProductAttrValueEntity::getAttrId
        ).collect(Collectors.toList());
        List<Long> searchAttrIds= attrService.selectSearchAttrs(attrIds);
        Set<Long> idSet = new HashSet<>(searchAttrIds);

        List<SkuEsModel.Attrs> attrsList= productAttrValueEntities.stream().filter(
                item -> {
                    return idSet.contains(item.getAttrId());
                }
        ).map(
                item -> {
                    SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
                    BeanUtils.copyProperties(item, attrs1);
                    return attrs1;
                }
        ).collect(Collectors.toList());
        //TODO 1：发送远程调用，库存系统查询是否有库存
        Map<Long, Boolean> stockMap = null;
        try {
            R skuHasStock = wareFeignService.getSkusHasStock(skuIdList);
            //
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {};
            stockMap = skuHasStock.getData(typeReference).stream()
                    .collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock(), (entity1, entity2) -> entity1));
        } catch (Exception e) {
            log.error("库存服务查询异常：原因{}",e);
        }


        //分装每个sku的信息
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> collect = skus.stream().map(
                sku -> {
                    SkuEsModel skuEsModel = new SkuEsModel();
                    BeanUtils.copyProperties(sku,skuEsModel);
                    skuEsModel.setSkuPrice(sku.getPrice());
                    skuEsModel.setSkuImg(sku.getSkuDefaultImg());
                    if (finalStockMap ==null){
                        skuEsModel.setHasStock(true);
                    }
                    //库存
                    skuEsModel.setHasStock(finalStockMap.get(sku.getSkuId()));


                    //TODO 2: 热度评分，0、
                    skuEsModel.setHotScore(0L);
                    //TODO 3: 查询品牌和分类的名字信息
                    BrandEntity brand = brandService.getById(skuEsModel.getBrandId());
                    skuEsModel.setBrandName(brand.getName());
                    skuEsModel.setBrandImg(brand.getLogo());
                    CategoryEntity categoryEntity = categoryService.getById(skuEsModel.getCatalogId());
                    skuEsModel.setCatalogName(categoryEntity.getName());
                    //设置检索属性
                    skuEsModel.setAttrs(attrsList);

                    return  skuEsModel;
                }
        ).collect(Collectors.toList());
        //TODO 5: 将数据发给es进行保存，mell-search
        R r = searchFeignServer.productStatsUP(collect);
        if (r.getCode() == 0){
            //TODO 6: 修改当前spu的状态
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }else {
            //远程调用失败
            //TODO 7:重复调用？接口幂函数等
        }


    }


}