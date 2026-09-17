package com.changgeng.service;

import com.alibaba.fastjson.JSON;
import com.changgeng.mapper.BenchmarkMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BenchmarkService {
    @Resource
    private BenchmarkMapper benchmarkMapper;


    public List<Map<String,Object>> getTarget(Integer nodeId) {
        return benchmarkMapper.getTarget(nodeId);
    }

    public List<Map> getEvaluation(Integer nodeId) {
        return benchmarkMapper.getEvaluation(nodeId);
    }

    public  List<Map> getEvaluationList(Integer unitId,Date startDate,Date endDate){
        List<Map>  list= benchmarkMapper.getEvaluationList(unitId,startDate,endDate);
        if(CollectionUtils.isEmpty(list)){
            return new ArrayList<>();
        }
        for(Map map:list){
            Integer evaluationId=Integer.valueOf (((Long) map.get("id")).toString());
            Map<String,Map<String, Object>>    benchmarkFactors=   benchmarkMapper.getBenchmarkFactorByEvaluationId(evaluationId).stream().collect(Collectors.toMap(d->d.get("model_id").toString(),a->a,(a1,a2)->a1));
            Map<String,Map<String, Object>>    benchmarkRanges= benchmarkMapper.getBenchmarkRangeByEvaluationId(evaluationId).stream().collect(Collectors.toMap(d->d.get("model_id").toString(),a->a,(a1,a2)->a1));
            Map<String,Map<String, Object>>  benchmarkTargets=  benchmarkMapper.getBenchmarkTargetByEvaluationId(evaluationId).stream().collect(Collectors.toMap(d->d.get("model_id").toString(),a->a,(a1,a2)->a1));
            List<Map<String, Object>>  objectTargets=  benchmarkMapper.getObjectTargetByEvaluationId(evaluationId);
            List<Map<String, Object>>  objectRanges=  benchmarkMapper.getObjectRangeByEvaluationId(evaluationId);
            List<Map<String, Object>>  objectFactors=  benchmarkMapper.getObjectFactorByEvaluationId(evaluationId);
            objectTargets.forEach(d->{
                Map<String, Object> benchmarkTarget=benchmarkTargets.get(d.get("model_id").toString());
                  Double value=Double.valueOf(d.get("standard_value").toString());
                  Double valueBenchmark=Double.valueOf(benchmarkTarget.get("standard_value").toString());
                  d.put("benchmark_value",valueBenchmark);
                  d.put("difference",Math.abs(valueBenchmark-value));
            });
            map.put("target",objectTargets);
            objectRanges.forEach(d->{
                Map<String, Object> benchmarkRange=benchmarkRanges.get(d.get("model_id").toString());
                Double value=Double.valueOf(d.get("standard_value").toString());
                Double valueBenchmark=Double.valueOf(benchmarkRange.get("standard_value").toString());
                d.put("benchmark_value",valueBenchmark);
                d.put("difference",Math.abs(valueBenchmark-value));
            });
            map.put("range",objectRanges);
            objectFactors.forEach(d->{
                Map<String, Object> benchmarkFactor=benchmarkFactors.get(d.get("model_id").toString());
                Double value=Double.valueOf(d.get("standard_value").toString());
                Double valueBenchmark=Double.valueOf(benchmarkFactor.get("standard_value").toString());
                d.put("benchmark_value",valueBenchmark);
                d.put("difference",Math.abs(valueBenchmark-value));
            });
            map.put("factor",objectFactors);
        }
        return list;
    }

    public  List<Map> getBenchmarkByTagCode(String tagCode){
      Map map=benchmarkMapper.getEvaluationIdByTagCode(tagCode);
      return benchmarkMapper.getBenchmarkValue((Long)map.get("id"),(Long)map.get("model_id"));
    }
    public  List<Map> getRangeValue( Long evaluationId){
        return benchmarkMapper.getRangeValue(evaluationId);
    }

    public  Map getLastEvaluation( Long evaluationId){
        Map map=new HashMap();
        Map old=new HashMap();
        map.put("历史",old);
        Long id=benchmarkMapper.getLastEvaluation(evaluationId);
        old.put("目标",benchmarkMapper.getTargetValue(id));
        old.put("范围",benchmarkMapper.getRangeValue(id));
        Map last=new HashMap();
        map.put("当前",last);
        last.put("目标",benchmarkMapper.getTargetValue(evaluationId));
        last.put("范围",benchmarkMapper.getRangeValue(evaluationId));
        return map;
    }


    }
