package com.changgeng.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface BenchmarkMapper {
    List<Map<String, Object>> getTarget(@Param("nodeId") Integer nodeId);

    List<Map> getEvaluation(@Param("nodeId") Integer nodeId);

    List<Map> getEvaluationList(@Param("unitId") Integer unitId,@Param("startDate") Date startDate,@Param("endDate") Date endDate);

    List<Map<String, Object>> getBenchmarkTargetByEvaluationId(@Param("evaluationId") Integer evaluationId);

    List<Map<String, Object>> getBenchmarkRangeByEvaluationId(@Param("evaluationId") Integer evaluationId);

    List<Map<String, Object>> getBenchmarkFactorByEvaluationId(@Param("evaluationId") Integer evaluationId);

    List<Map<String, Object>> getObjectTargetByEvaluationId(@Param("evaluationId") Integer evaluationId);

    List<Map<String, Object>> getObjectRangeByEvaluationId(@Param("evaluationId") Integer evaluationId);

    List<Map<String, Object>> getObjectFactorByEvaluationId(@Param("evaluationId") Integer evaluationId);
    Map getEvaluationIdByTagCode(@Param("tagCode") String tagCode);
    List<Map> getBenchmarkValue(@Param("evaluationId") Long evaluationId,@Param("modelId") Long modelId);
    List<Map> getRangeValue(@Param("evaluationId") Long evaluationId);
    Long getLastEvaluation(@Param("evaluationId") Long evaluationId);
    List<Map> getTargetValue(@Param("evaluationId") Long evaluationId);


}
