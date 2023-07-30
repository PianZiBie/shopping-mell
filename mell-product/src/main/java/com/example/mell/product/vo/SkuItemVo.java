package com.example.mell.product.vo;

import com.example.mell.product.entity.SkuImagesEntity;
import com.example.mell.product.entity.SkuInfoEntity;
import com.example.mell.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;
@Data
public class SkuItemVo {
    SkuInfoEntity info;
    List<SkuImagesEntity> images;
    List<SkuItemSaleAttrVo> saleAttr;
    SpuInfoDescEntity desc;
    List<SpuItemAttrGroupVo> groupAttrs;
    @Data
    public static class SkuItemSaleAttrVo{
        private Long attrId;
        private String attrName;
        private List<AttrValueWithSkuIdVo> attrValues;

    }
    @Data
    public static class SpuItemAttrGroupVo{
        private String groupName;
        private List<SpuBaseAttrVo> attrs;



    }
    @Data
    public static class SpuBaseAttrVo{
        private Long attrId;
        private String attrName;
        private String attrValue;

    }


}
