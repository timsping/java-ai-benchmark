package com.changgeng.service;

import com.alibaba.fastjson.JSON;
import com.changgeng.mapper.BenchmarkMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BenchmarkService {
    @Resource
    private BenchmarkMapper benchmarkMapper;


    public List<Map<String,Object>> getTarget(Integer nodeId) {
        List<Map<String,Object>> result = new ArrayList<>();
        result = benchmarkMapper.getTarget(nodeId);
       /* //先准备测试数据
        result = JSON.parseArray("\t[\n" +
                "\t\t{\n" +
                "\t\t\t\"targetId\": 167738,\n" +
                "\t\t\t\"name\": \"目标\",\n" +
                "\t\t\t\"direction\": \"越大越好\",\n" +
                "\t\t\t\"unit\": null,\n" +
                "\t\t\t\"isVariance\": \"是\",\n" +
                "\t\t\t\"objectId\": 167737,\n" +
                "\t\t\t\"varianceValue\": 0.3,\n" +
                "\t\t\t\"tagCode\": \"DC01M0101BZ4F20001\",\n" +
                "\t\t\t\"tagName\": \"#2机锅炉热效率（反平衡）\",\n" +
                "\t\t}\n" +
                "\t]",Map.class);*/
        return result;
    }
}
