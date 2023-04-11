package com.example.mell.product.service.impl;

import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.mell.product.dao.AttrGroupDao;
import com.example.mell.product.entity.AttrGroupEntity;
import com.example.mell.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.mell.product.dao.CategoryDao;
import com.example.mell.product.entity.CategoryEntity;
import com.example.mell.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Resource
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> entities = baseMapper.selectList(null);
        List<CategoryEntity> collect = entities.stream().filter(item -> {
            return item.getParentCid() == 0;
        }).map(menu -> {
            menu.setChildren(getChildren(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 查询子菜单方法
     *
     * @param root
     * @param all
     * @return
     */
    public List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(item -> {
            return item.getParentCid() == root.getCatId();
        }).map(menu -> {
            menu.setChildren(getChildren(menu, all));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return children;
    }

    @Override
    public void removeMenuByIds(List<Long> list) {
        //1：检查当前的菜单是否被别的地方引用

        baseMapper.deleteBatchIds(list);
    }

    /**
     * 级联更新所有数据
     *
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        CategoryEntity categoryEntity = this.baseMapper.selectOne(new QueryWrapper<CategoryEntity>().eq("cat_id", catelogId));
        List<CategoryEntity> all = this.list();
        List<Long> father = getFather(categoryEntity, all);
        Collections.reverse(father);
        father.add(catelogId);
        Long[] longs = new Long[father.size()];
        for (int i = 0; i < father.size(); i++) {
            longs[i]=Long.valueOf(father.get(i).toString());
        }
        return longs;

    }

    /**
     * catelogId寻找父类id
     * @param root 当前catelog类
     * @param all  CategoryEntity集合
     * @return
     */

    public List<Long> getFather(CategoryEntity root, List<CategoryEntity> all) {

        List<Long> collect = new ArrayList<>();
        all.stream().filter(item -> {
            return item.getCatId() == root.getParentCid();
        }).forEach(item -> {
            collect.add(item.getCatId());
            if (item.getParentCid() != 0) {
                collect.addAll(getFather(item, all));
            }
        });
        return collect;
    }


}
