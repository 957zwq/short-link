package com.winesasfood.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winesasfood.project.dao.entity.ShortLinkDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 短链接持久层
 */
public interface ShortLinkMapper extends BaseMapper<ShortLinkDO> {

    /**
     * 增加短链接点击量
     *
     * @param fullShortUrl 完整短链接
     */
    @Update("UPDATE t_link SET click_num = click_num + 1 WHERE full_short_url = #{fullShortUrl} AND del_flag = 0")
    void incrementClickNum(@Param("fullShortUrl") String fullShortUrl);
}
