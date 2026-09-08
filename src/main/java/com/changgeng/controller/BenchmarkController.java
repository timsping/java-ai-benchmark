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
        Map<String, Object> map = new HashMap<>(3);
        map.put("code", "200");
        map.put("message", "操作成功");
        map.put("success", true);
        List<Map> result = new ArrayList<>();
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
    public Map<String, Object> getEvaluation(@RequestBody Map<String, Object> param){
        Integer nodeId = (Integer) param.get("nodeId");
        log.error("getEvaluation 参数 nodeId {}",nodeId);
        Map<String, Object> map = new HashMap<>(3);
        map.put("code", "200");
        map.put("message", "操作成功");
        map.put("success", true);
        //获取相同工况下需要调整的因素
        List<Map> result = new ArrayList<>();
        if(nodeId == 167737){
            result = JSON.parseArray("[\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"factorid\": 32267439,\n" +
                    "\t\t\t\t\"name\": \"空预器A侧出口烟气含氧量\",\n" +
                    "\t\t\t\t\"objectvalue\": 5.790687275,\n" +
                    "\t\t\t\t\"benchmarkvalue\": 5.731624794,\n" +
                    "\t\t\t\t\"offsetvalue\": 0.059062481,\n" +
                    "\t\t\t\t\"count\": 0,\n" +
                    "\t\t\t\t\"unit\": \"%\",\n" +
                    "\t\t\t\t\"offsetPercentage\": 5.0,\n" +
                    "\t\t\t\t\"direction\": null,\n" +
                    "\t\t\t\t\"effect\": \"有效\",\n" +
                    "\t\t\t\t\"adjust\": \"可调\",\n" +
                    "\t\t\t\t\"longCode\": null,\n" +
                    "\t\t\t\t\"hasChildren\": false,\n" +
                    "\t\t\t\t\"parentId\": 0,\n" +
                    "\t\t\t\t\"modelId\": 927972,\n" +
                    "\t\t\t\t\"children\": [],\n" +
                    "\t\t\t\t\"tagCode\": \"DC01M0101K1Q1C0BQ1Y10001\"\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"factorid\": 32267441,\n" +
                    "\t\t\t\t\"name\": \"空预器出口氧量均值\",\n" +
                    "\t\t\t\t\"objectvalue\": 5.2424332502499999,\n" +
                    "\t\t\t\t\"benchmarkvalue\": 5.222411811000001,\n" +
                    "\t\t\t\t\"offsetvalue\": 0.0200214392499985,\n" +
                    "\t\t\t\t\"count\": 0,\n" +
                    "\t\t\t\t\"unit\": \"%\",\n" +
                    "\t\t\t\t\"offsetPercentage\": 1.0,\n" +
                    "\t\t\t\t\"direction\": null,\n" +
                    "\t\t\t\t\"effect\": \"无效\",\n" +
                    "\t\t\t\t\"adjust\": \"可调\",\n" +
                    "\t\t\t\t\"longCode\": null,\n" +
                    "\t\t\t\t\"hasChildren\": false,\n" +
                    "\t\t\t\t\"parentId\": 0,\n" +
                    "\t\t\t\t\"modelId\": 928100,\n" +
                    "\t\t\t\t\"children\": [],\n" +
                    "\t\t\t\t\"tagCode\": \"DC01M0101K0Q0C0BQ1Y10001\"\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"factorid\": 32267442,\n" +
                    "\t\t\t\t\"name\": \"空预器B侧出口烟气含氧量\",\n" +
                    "\t\t\t\t\"objectvalue\": 4.70566368125,\n" +
                    "\t\t\t\t\"benchmarkvalue\": 4.700825373333333,\n" +
                    "\t\t\t\t\"offsetvalue\": 0.004838307916667,\n" +
                    "\t\t\t\t\"count\": 0,\n" +
                    "\t\t\t\t\"unit\": \"%\",\n" +
                    "\t\t\t\t\"offsetPercentage\": 1.0,\n" +
                    "\t\t\t\t\"direction\": null,\n" +
                    "\t\t\t\t\"effect\": \"有效\",\n" +
                    "\t\t\t\t\"adjust\": \"可调\",\n" +
                    "\t\t\t\t\"longCode\": null,\n" +
                    "\t\t\t\t\"hasChildren\": false,\n" +
                    "\t\t\t\t\"parentId\": 0,\n" +
                    "\t\t\t\t\"modelId\": 927976,\n" +
                    "\t\t\t\t\"children\": [],\n" +
                    "\t\t\t\t\"tagCode\": \"DC01M0101K2Q2C0BQ1Y10001\"\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"factorid\": 32267443,\n" +
                    "\t\t\t\t\"name\": \"空预器B出口烟气温度\",\n" +
                    "\t\t\t\t\"objectvalue\": 145.63332112633334,\n" +
                    "\t\t\t\t\"benchmarkvalue\": 147.43752343314285,\n" +
                    "\t\t\t\t\"offsetvalue\": -1.80420230680951,\n" +
                    "\t\t\t\t\"count\": 0,\n" +
                    "\t\t\t\t\"unit\": \"℃\",\n" +
                    "\t\t\t\t\"offsetPercentage\": 6.0,\n" +
                    "\t\t\t\t\"direction\": null,\n" +
                    "\t\t\t\t\"effect\": \"有效\",\n" +
                    "\t\t\t\t\"adjust\": \"可调\",\n" +
                    "\t\t\t\t\"longCode\": null,\n" +
                    "\t\t\t\t\"hasChildren\": false,\n" +
                    "\t\t\t\t\"parentId\": 0,\n" +
                    "\t\t\t\t\"modelId\": 927968,\n" +
                    "\t\t\t\t\"children\": [],\n" +
                    "\t\t\t\t\"tagCode\": \"DC01M0101K2Q2C0BT0A00001\"\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"factorid\": 32267440,\n" +
                    "\t\t\t\t\"name\": \"空预器A出口烟气温度\",\n" +
                    "\t\t\t\t\"objectvalue\": 138.08206049599998,\n" +
                    "\t\t\t\t\"benchmarkvalue\": 140.02043969285715,\n" +
                    "\t\t\t\t\"offsetvalue\": -1.93837919685717,\n" +
                    "\t\t\t\t\"count\": 0,\n" +
                    "\t\t\t\t\"unit\": \"℃\",\n" +
                    "\t\t\t\t\"offsetPercentage\": 6.0,\n" +
                    "\t\t\t\t\"direction\": null,\n" +
                    "\t\t\t\t\"effect\": \"有效\",\n" +
                    "\t\t\t\t\"adjust\": \"可调\",\n" +
                    "\t\t\t\t\"longCode\": null,\n" +
                    "\t\t\t\t\"hasChildren\": false,\n" +
                    "\t\t\t\t\"parentId\": 0,\n" +
                    "\t\t\t\t\"modelId\": 927964,\n" +
                    "\t\t\t\t\"children\": [],\n" +
                    "\t\t\t\t\"tagCode\": \"DC01M0101K1Q1C0BT0A00001\"\n" +
                    "\t\t\t}\n" +
                    "\t\t]",Map.class);
        }else {
            map.put("message","工况稳定，暂时没有可优化项");
        }
        map.put("data",result);
        return map;
    }

}
