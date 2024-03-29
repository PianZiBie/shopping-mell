package com.example.mell.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 2级分类
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catalog2Vo {
    private String catalogId;//1级夫=父分类id
    private List<Catalog3Vo> catalog3List;//三级子分类
    private String id;
    private String name;

    /**
     * 3级分类
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Catalog3Vo{
        private String catalog2Id;//1级夫=父分类id
        private String id;
        private String name;
    }
}
