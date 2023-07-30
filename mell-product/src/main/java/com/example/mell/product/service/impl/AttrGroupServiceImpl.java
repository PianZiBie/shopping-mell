package com.example.mell.product.service.impl;

import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.mell.product.entity.AttrEntity;
import com.example.mell.product.service.AttrService;
import com.example.mell.product.vo.AttrGroupWithAttrsVo;
import com.example.mell.product.vo.SkuItemVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.mell.product.dao.AttrGroupDao;
import com.example.mell.product.entity.AttrGroupEntity;
import com.example.mell.product.service.AttrGroupService;

import javax.annotation.Resource;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Resource
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            queryWrapper.and(obj -> {
                obj.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }

        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    queryWrapper
            );
            return new PageUtils(page);

        } else {
            queryWrapper.eq("catelog_id", catelogId);

            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    queryWrapper);

            return new PageUtils(page);
        }


    }

    /**
     * 根据分类id 查出分组及子信息
     *
     * @param catelogId
     * @return
     */
// 获取属性组及其属性
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        // 查询分组信息
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        // 将每个 AttrGroupEntity 对象转换为 AttrGroupWithAttrsVo 对象
        List<AttrGroupWithAttrsVo> collect = attrGroupEntities.stream().map(
                item -> {
                    AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
                    BeanUtils.copyProperties(item, attrGroupWithAttrsVo);

                    // 获取与该属性组关联的所有属性
                    List<AttrEntity> relationAttr = attrService.getRelationAttr(attrGroupWithAttrsVo.getAttrGroupId());
                    attrGroupWithAttrsVo.setAttrs(relationAttr);

                    // 如果没有属性，则将该对象从列表中删除
                    if (relationAttr == null) {
                        return null;
                    }

                    return attrGroupWithAttrsVo;
                }
        ).collect(Collectors.toList());

        // 从列表中删除空对象
        collect.removeIf(Objects::isNull);

        // 返回列表
        return collect;
    }

    @Override
    public List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {

        //查出当前spu对应的所有属性分组
        AttrGroupDao baseMapper = this.getBaseMapper();
        List<SkuItemVo.SpuItemAttrGroupVo> vos =baseMapper.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
        return vos;
    }
}