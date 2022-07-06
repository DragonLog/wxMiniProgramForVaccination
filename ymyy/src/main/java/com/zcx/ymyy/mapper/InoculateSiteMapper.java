package com.zcx.ymyy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcx.ymyy.entity.InoculateSite;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

public interface InoculateSiteMapper extends BaseMapper<InoculateSite> {

    @Select("SELECT a.*, " +
            "(SELECT b.name FROM district b WHERE b.code = a.province_code) AS province, " +
            "(SELECT b.name FROM district b WHERE b.code = a.city_code) AS city, " +
            "(SELECT b.name FROM district b WHERE b.code = a.district_code) AS district " +
            "FROM inoculate_site a")
    List<Map<String, Object>> selectAll();

}
