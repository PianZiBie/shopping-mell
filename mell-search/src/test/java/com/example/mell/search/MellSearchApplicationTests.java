package com.example.mell.search;

import com.alibaba.fastjson.JSON;
import com.example.common.to.MemberPrice;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class MellSearchApplicationTests {
    @Autowired
    private RestHighLevelClient client;

    @Test
    public void contextLoads() {
        System.out.println(client);
    }

    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("user");
        indexRequest.id("1");
        MemberPrice memberPrice = new MemberPrice();
        memberPrice.setId(1L);
        memberPrice.setName("张三");
        String jsonString = JSON.toJSONString(memberPrice);
        indexRequest.source(jsonString, XContentType.JSON);
        IndexResponse index = client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(index);
    }

    @Test
    public void searchData() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("hotel");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(search);

    }


}
