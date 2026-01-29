package com.winesasfood.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winesasfood.admin.dao.entity.GroupDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短链接分组Mapper
 */
@Mapper
public interface GroupMapper extends BaseMapper<GroupDO> {
}
