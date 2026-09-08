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
        return benchmarkMapper.getTarget(nodeId);
    }

    public List<Map> getEvaluation(Integer nodeId) {
        return benchmarkMapper.getEvaluation(nodeId);
    }
}
