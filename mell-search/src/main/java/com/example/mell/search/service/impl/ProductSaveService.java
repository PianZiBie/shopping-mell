package com.example.mell.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.shaded.com.google.gson.JsonObject;
import com.example.common.to.es.SkuEsModel;
import com.example.mell.search.config.mellElasticSearchConfig;
import com.example.mell.search.constant.EsConstant;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductSaveService implements com.example.mell.search.service.ProductSaveService {
    @Resource
    RestHighLevelClient restHighLevelClient;

    @Override
    public boolean productStatsUP(List<SkuEsModel> skuEsModels) throws IOException {
        //保存到es
        //1:给es建立索引,product,建立好映射关系
        //2:给es中保存数据
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel model : skuEsModels) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(model.getSkuId().toString());
            String s = JSON.toJSONString(model);
            indexRequest.source(s, XContentType.JSON);

            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, mellElasticSearchConfig.COMM_OPTIONS);
        //TODO 处理批量错误
        boolean b = bulk.hasFailures();
        List<String> collect = Arrays.stream(bulk.getItems()).map(
                BulkItemResponse::getId
        ).collect(Collectors.toList());
        log.info("商品上架成功：{}", collect);
        return b;

    }
}
