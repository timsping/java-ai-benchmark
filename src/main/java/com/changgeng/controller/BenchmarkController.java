package com.changgeng.controller;

import com.alibaba.fastjson.JSON;
import com.changgeng.common.result.Result;
import com.changgeng.service.BenchmarkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/benchmark")
@Slf4j
public class BenchmarkController {

    @Resource
    private BenchmarkService benchmarkService;

    /**
     * 根据节点id获取对应的目标
     * @param param
     *          nodeId 实体节点id
     * @return
     */
    @RequestMapping("/target")
    public Result getTarget(@RequestBody Map<String, Object> param){
        Integer nodeId = (Integer) param.get("nodeId");
        log.error("getTarget 参数 nodeId {}",nodeId);
        //查询实体节点下对应的标杆
        if(CollectionUtils.isEmpty(param) || nodeId == null){
            return  Result.error("实体节点不存在！ 获取失败");
        }
        List<Map<String, Object>> targetList = benchmarkService.getTarget(nodeId);
        log.error("获取到目标数据 {}" , targetList.size());
        if(CollectionUtils.isEmpty(targetList)){
            return Result.error("没有找到此节点下对应的目标，请确认是否存在");
        }
        return Result.success(targetList);
    }

    /**
     * 找到同一目标同工况下评估单因素对比信息
     * @param param
     *          nodeId 标杆id
     * @return
     */
    @RequestMapping("/evaluation")
    public Result getEvaluation(@RequestBody Map<String, Object> param){
        Integer nodeId = (Integer) param.get("nodeId");
        log.error("getEvaluation 参数 nodeId {}",nodeId);
        List<Map> evaluation = benchmarkService.getEvaluation(nodeId);
        if(CollectionUtils.isEmpty(evaluation)){
            return Result.error("工况稳定，暂时没有可优化项");
        }
        return Result.success(evaluation);
    }

    /**
     * 获取时间段的评估单信息
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping("/getEvaluationList")
    public Result getEvaluationList(@RequestBody Map<String, Object> param) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Integer  unitId=null;
        if(null!=param.get("unitId")){
            unitId=Integer.valueOf(param.get("unitId").toString());
        }
        List<Map> evaluation = benchmarkService.getEvaluationList( unitId,sdf.parse(param.get("startDate").toString()), sdf.parse(param.get("endDate").toString()));
        return Result.success(evaluation);
    }
    /**
     * 获取指标的标杆信息
     * @param tagCode
     * @return
     */
    @RequestMapping("/getBenchmarkByTagCode")
    public Result getBenchmarkByTagCode(@RequestBody Map<String, Object> param) {
        List<Map> evaluation = benchmarkService.getBenchmarkByTagCode( param.get("tagCode").toString());
        return Result.success(evaluation);
    }
    /**
     * 获取评估单的范围数据
     * @param tagCode
     * @return
     */
    @RequestMapping("/getRangeValue")
    public Result getRangeValue(@RequestBody Map<String, Object> param) {
        List<Map> evaluation = benchmarkService.getRangeValue( Long.valueOf(param.get("evaluationId").toString()));
        return Result.success(evaluation);
    }
    /**
     * 获取历史和最新的寻优单记录
     * @param tagCode
     * @return
     */
    @RequestMapping("/getLastEvaluation")
    public Result getLastEvaluation(@RequestBody Map<String, Object> param) {
        Map evaluation = benchmarkService.getLastEvaluation( Long.valueOf(param.get("evaluationId").toString()));
        return Result.success(evaluation);
    }

}
