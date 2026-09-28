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
    Map getEvaluationIdByTagCode(@Param("tagCode") String tagCode,@Param("startDate") Date startDate,@Param("endDate") Date endDate);
    List<Map> getBenchmarkValue(@Param("evaluationId") Long evaluationId,@Param("modelId") Long modelId);
    List<Map> getRangeValue(@Param("evaluationId") Long evaluationId);
    Long getLastEvaluation(@Param("evaluationId") Long evaluationId);
    List<Map> getTargetValue(@Param("evaluationId") Long evaluationId);

    List<Map> getBenchmarkDataIdByRange(@Param("value") Double value,@Param("evaluationId") Long evaluationId,@Param("text") String text);
    List<Map> getBenchmarkDataTarget(@Param("mainId") Long mainId,@Param("datas") List<Integer> datas,@Param("direction") String direction);
    List<Map> getBenchmarkDataRange(@Param("mainId") Long mainId,@Param("dataId") Integer dataId);
    List<Map> getBenchmarkDataFactor(@Param("mainId") Long mainId,@Param("dataId") Integer dataId);

    /**
     * 获取所有标杆标准值和范围
     * @return
     */
    List<Map> getAllStandardValueAndRange();

    /**
     * 根据名称，获取最新的5条寻优记录
     * @param name
     * @return
     */
    List<Map<String, Object>> getLast5BenchmarkRecord(@Param("name") String name);

}
