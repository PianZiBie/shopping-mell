package com.example.mell.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.mell.product.service.CategoryBrandRelationService;
import com.example.mell.product.vo.Catalog2Vo;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
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

    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    RedissonClient redissonClient;

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
     *缓存失效模式
     * @param category
     */
//    @Caching(evict = {
//            @CacheEvict(value = {"category"},key = "'getLevelCategorys'"),
//            @CacheEvict(value = {"category"},key = "'getLevelCategorys'")
//    })
    @CacheEvict(value = {"category"},allEntries = true)
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
            longs[i] = Long.valueOf(father.get(i).toString());
        }
        return longs;

    }

    /**
     * 指定key 指定缓存时间 指定缓存数据格式
     * @return
     */
    @Cacheable(value = {"category"},key = "#root.method.name")//每一个需要缓存的数据我们都要指定缓存区域
    @Override
    public List<CategoryEntity> getLevelCategorys() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedissonLock() {
        RLock lock = redissonClient.getLock("catalogJson-lock");
        lock.lock(30, TimeUnit.SECONDS);
        Map<String, List<Catalog2Vo>> dataFromDb = null;
        try {
            dataFromDb = getCatalogJsonFromDb();
        } catch (Exception e) {

        } finally {
            lock.unlock();
        }
        return dataFromDb;
    }

    /**
     * 从数据库查询并封装数据::分布式锁
     *
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedisLock() {

        //1、占分布式锁。去redis占坑      设置过期时间必须和加锁是同步的，保证原子性（避免死锁）
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式锁成功...");
            Map<String, List<Catalog2Vo>> dataFromDb = null;
            try {
                //加锁成功...执行业务
                dataFromDb = getCatalogJsonFromDb();
            } finally {
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

                //删除锁
                stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);

            }
            //先去redis查询下保证当前的锁是自己的
            //获取值对比，对比成功删除=原子性 lua脚本解锁
            // String lockValue = stringRedisTemplate.opsForValue().get("lock");
            // if (uuid.equals(lockValue)) {
            //     //删除我自己的锁
            //     stringRedisTemplate.delete("lock");
            // }

            return dataFromDb;
        } else {
            System.out.println("获取分布式锁失败...等待重试...");
            //加锁失败...重试机制
            //休眠一百毫秒
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatalogJsonFromDbWithRedisLock();     //自旋的方式
        }
    }

    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {


        String catalogJSON = stringRedisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            System.out.println("缓存不命中...查询数据库...");
            Map<String, List<Catalog2Vo>> catalogJsonFromDb = getCatalogJsonFromDbWithRedissonLock();
            String jsonString = JSON.toJSONString(catalogJsonFromDb);
            stringRedisTemplate.opsForValue().set("catalogJSON", jsonString);

        }
        System.out.println("缓存命中...直接返回...");
        Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catalog2Vo>>>() {
        });
        return result;
    }
    @Cacheable(value = {"category"},key = "#root.method.name")
    public Map<String, List<Catalog2Vo>> getCatalogJson2() {


        List<CategoryEntity> categoryEntities1 = baseMapper.selectList(null);


        //查出所有1级分类
        List<CategoryEntity> levelCategorys = getParent_cid(categoryEntities1, 0L);
        Map<String, List<Catalog2Vo>> parentCid = levelCategorys.stream().collect(Collectors.toMap(
                key -> {
                    return key.getCatId().toString();
                },
                value -> {
                    List<CategoryEntity> categoryEntities = getParent_cid(categoryEntities1, value.getCatId());
                    List<Catalog2Vo> collect = null;
                    if (categoryEntities != null) {
                        collect = categoryEntities.stream().map(item -> {
                            Catalog2Vo catalog2Vo = new Catalog2Vo(value.getCatId().toString(), null, item.getCatId().toString(), item.getName());
                            List<CategoryEntity> categoryEntities3 = getParent_cid(categoryEntities1, item.getCatId());
                            if (categoryEntities3 != null) {
                                List<Catalog2Vo.Catalog3Vo> collect1 = categoryEntities3.stream().map(l3 -> {
                                    Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(item.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                                    return catalog3Vo;
                                }).collect(Collectors.toList());
                                catalog2Vo.setCatalog3List(collect1);
                            }
                            return catalog2Vo;
                        }).collect(Collectors.toList());

                    }

                    return collect;
                }
        ));
        return parentCid;
    }


    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDb() {

        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJson)) {
            //缓存不为空直接返回
            Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {
            });

            return result;
        }

        System.out.println("查询了数据库");


        List<CategoryEntity> categoryEntities1 = baseMapper.selectList(null);


        //查出所有1级分类
        List<CategoryEntity> levelCategorys = getParent_cid(categoryEntities1, 0L);
        Map<String, List<Catalog2Vo>> parentCid = levelCategorys.stream().collect(Collectors.toMap(
                key -> {
                    return key.getCatId().toString();
                },
                value -> {
                    List<CategoryEntity> categoryEntities = getParent_cid(categoryEntities1, value.getCatId());
                    List<Catalog2Vo> collect = null;
                    if (categoryEntities != null) {
                        collect = categoryEntities.stream().map(item -> {
                            Catalog2Vo catalog2Vo = new Catalog2Vo(value.getCatId().toString(), null, item.getCatId().toString(), item.getName());
                            List<CategoryEntity> categoryEntities3 = getParent_cid(categoryEntities1, item.getCatId());
                            if (categoryEntities3 != null) {
                                List<Catalog2Vo.Catalog3Vo> collect1 = categoryEntities3.stream().map(l3 -> {
                                    Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(item.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                                    return catalog3Vo;
                                }).collect(Collectors.toList());
                                catalog2Vo.setCatalog3List(collect1);
                            }
                            return catalog2Vo;
                        }).collect(Collectors.toList());

                    }

                    return collect;
                }
        ));
        return parentCid;
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> list, long parent_cid) {
        List<CategoryEntity> collect = list.stream().filter(
                item -> {
                    return item.getParentCid() == parent_cid;
                }
        ).collect(Collectors.toList());
        return collect;

    }

    /**
     * catelogId寻找父类id
     *
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
