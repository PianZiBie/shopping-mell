package com.example.mell.search.service;



import com.example.mell.search.vo.SearchParam;
import com.example.mell.search.vo.SearchResult;


public interface MallSearchService {
    SearchResult search(SearchParam param);
}
