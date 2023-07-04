package com.example.mell.search.service;

import com.example.common.to.es.SkuEsModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


public interface ProductSaveService {
    boolean productStatsUP(List<SkuEsModel> skuEsModels) throws IOException;
}
