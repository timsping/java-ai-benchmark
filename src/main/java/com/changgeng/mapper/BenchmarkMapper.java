package com.changgeng.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BenchmarkMapper {
    List<Map<String, Object>> getTarget(@Param("nodeId") Integer nodeId);

    List<Map> getEvaluation(@Param("nodeId") Integer nodeId);
}
