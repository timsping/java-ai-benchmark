package com.changgeng.service;

import com.alibaba.fastjson.JSON;
import com.changgeng.client.DamExtClient;
import com.changgeng.mapper.BenchmarkMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class BenchmarkService {
    @Resource
    private BenchmarkMapper benchmarkMapper;
    @Autowired
    private DamExtClient damExtClient;


    public List<Map<String, Object>> getTarget(Integer nodeId) {
        return benchmarkMapper.getTarget(nodeId);
    }

    public List<Map> getEvaluation(Integer nodeId) {
        return benchmarkMapper.getEvaluation(nodeId);
    }

    public List<Map> getEvaluationList(Integer unitId, Date startDate, Date endDate) {
        List<Map> list = benchmarkMapper.getEvaluationList(unitId, startDate, endDate);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        for (Map map : list) {
            Integer evaluationId = Integer.valueOf(((Long) map.get("id")).toString());
            Map<String, Map<String, Object>> benchmarkFactors = benchmarkMapper.getBenchmarkFactorByEvaluationId(evaluationId).stream().collect(Collectors.toMap(d -> d.get("model_id").toString(), a -> a, (a1, a2) -> a1));
            Map<String, Map<String, Object>> benchmarkRanges = benchmarkMapper.getBenchmarkRangeByEvaluationId(evaluationId).stream().collect(Collectors.toMap(d -> d.get("model_id").toString(), a -> a, (a1, a2) -> a1));
            Map<String, Map<String, Object>> benchmarkTargets = benchmarkMapper.getBenchmarkTargetByEvaluationId(evaluationId).stream().collect(Collectors.toMap(d -> d.get("model_id").toString(), a -> a, (a1, a2) -> a1));
            List<Map<String, Object>> objectTargets = benchmarkMapper.getObjectTargetByEvaluationId(evaluationId);
            List<Map<String, Object>> objectRanges = benchmarkMapper.getObjectRangeByEvaluationId(evaluationId);
            List<Map<String, Object>> objectFactors = benchmarkMapper.getObjectFactorByEvaluationId(evaluationId);
            objectTargets.forEach(d -> {
                Map<String, Object> benchmarkTarget = benchmarkTargets.get(d.get("model_id").toString());
                Double value = Double.valueOf(d.get("standard_value").toString());
                Double valueBenchmark = Double.valueOf(benchmarkTarget.get("standard_value").toString());
                d.put("benchmark_value", valueBenchmark);
                d.put("difference", Math.abs(valueBenchmark - value));
            });
            map.put("target", objectTargets);
            objectRanges.forEach(d -> {
                Map<String, Object> benchmarkRange = benchmarkRanges.get(d.get("model_id").toString());
                Double value = Double.valueOf(d.get("standard_value").toString());
                Double valueBenchmark = Double.valueOf(benchmarkRange.get("standard_value").toString());
                d.put("benchmark_value", valueBenchmark);
                d.put("difference", Math.abs(valueBenchmark - value));
            });
            map.put("range", objectRanges);
            objectFactors.forEach(d -> {
                Map<String, Object> benchmarkFactor = benchmarkFactors.get(d.get("model_id").toString());
                Double value = Double.valueOf(d.get("standard_value").toString());
                Double valueBenchmark = Double.valueOf(benchmarkFactor.get("standard_value").toString());
                d.put("benchmark_value", valueBenchmark);
                d.put("difference", Math.abs(valueBenchmark - value));
            });
            map.put("factor", objectFactors);
        }
        return list;
    }

    public List<Map> getBenchmarkByTagCode(String tagCode) {
        Map map = benchmarkMapper.getEvaluationIdByTagCode(tagCode, null, null);
        return benchmarkMapper.getBenchmarkValue((Long) map.get("id"), (Long) map.get("model_id"));
    }

    public List<Map> getRangeValue(Long evaluationId) {
        return benchmarkMapper.getRangeValue(evaluationId);
    }

    public Map getLastEvaluation(Long evaluationId) {
        Map map = new HashMap();
        Map old = new HashMap();
        map.put("历史", old);
        Long id = benchmarkMapper.getLastEvaluation(evaluationId);
        old.put("目标", benchmarkMapper.getTargetValue(id));
        old.put("范围", benchmarkMapper.getRangeValue(id));
        Map last = new HashMap();
        map.put("当前", last);
        last.put("目标", benchmarkMapper.getTargetValue(evaluationId));
        last.put("范围", benchmarkMapper.getRangeValue(evaluationId));
        return map;
    }

    public List<Map> getHistoryEvaluation(String tagCode, Double value, String range) {
        Map map = benchmarkMapper.getEvaluationIdByTagCode(tagCode, null, null);
        Long evaluationId = (Long) map.get("id");
        List<Map> benchmarkDatas = benchmarkMapper.getBenchmarkDataIdByRange(value, evaluationId, range);
        List<Integer> datas = benchmarkDatas.stream().map(d -> (Integer) d.get("data_id")).collect(Collectors.toList());
        Long mainId = (Long) benchmarkDatas.get(0).get("id");
        List<Map> list = benchmarkMapper.getBenchmarkDataTarget(mainId, datas, map.get("direction").toString());
        list.forEach(d -> {
            d.put("range", benchmarkMapper.getBenchmarkDataRange(mainId, (Integer) d.get("data_id")));
            d.put("factor", benchmarkMapper.getBenchmarkDataFactor(mainId, (Integer) d.get("data_id")));
        });
        return list;
    }


    public String getSimilarityBenchmarkDetails(String userMessage) {
        Map<String, Object> similarityBenchmarkList = damExtClient.getSimilarityBenchmarkList(userMessage);
        // 1. 定义需要保留的字段白名单（Java 8 兼容写法）
        Map<String, String> kPropsKeepMap = new HashMap<>();
        kPropsKeepMap.put("名称", "name");
        kPropsKeepMap.put("描述", "description");
        kPropsKeepMap.put("单位", "unit");
        kPropsKeepMap.put("公式说明", "formula");
        kPropsKeepMap.put("别名", "alias");

        Map<String, String> mPropsFactorKeepMap = new HashMap<>();
        mPropsFactorKeepMap.put("有效性方向", "validityDirection");
        mPropsFactorKeepMap.put("重要性排序", "importanceRank");

// 2. 准备基准值映射（保持你原有逻辑不变）
        List<Map> allStandardValueAndRange = benchmarkMapper.getAllStandardValueAndRange();
        Map<String, Map> tagCodeMap = allStandardValueAndRange.stream()
                .collect(Collectors.toMap(
                        one -> one.get("tag_code").toString(),
                        one -> one,
                        (newOne, oldOne) -> newOne
                ));

// 3. 按 mType 分组存储重组后的数据
        Map<String, List<Map<String, Object>>> groupedResult = new LinkedHashMap<>();
        groupedResult.put("target", new ArrayList<>());
        groupedResult.put("range", new ArrayList<>());
        groupedResult.put("factor", new ArrayList<>());

        List<Map<String, Object>> javaResult = (List<Map<String, Object>>) similarityBenchmarkList.get("JAVAResult");
        Map<String, Object> bestMatch = (Map<String, Object>) similarityBenchmarkList.get("bestMatch");
        String name = bestMatch.get("名称").toString();
        List<Map<String, Object>> last5BenchmarkRecord = benchmarkMapper.getLast5BenchmarkRecord(name);

        for (Map<String, Object> curMap : javaResult) {
            Map<String, Object> rawKProps = curMap.get("kProps") instanceof Map
                    ? (Map<String, Object>) curMap.get("kProps") : Collections.emptyMap();
            Map<String, Object> rawMProps = curMap.get("mProps") instanceof Map
                    ? (Map<String, Object>) curMap.get("mProps") : Collections.emptyMap();
            String mType = String.valueOf(curMap.getOrDefault("mType", "unknown"));

            // 4. 构建扁平化指标对象（只提取白名单字段）
            Map<String, Object> indicator = new LinkedHashMap<>();

            // 提取 kProps 通用字段
            for (Map.Entry<String, String> entry : kPropsKeepMap.entrySet()) {
                Object val = rawKProps.get(entry.getKey());
                indicator.put(entry.getValue(), val != null ? val.toString() : "");
            }

            // 公式与别名合并：优先取公式说明，为空则取别名
            String formula = String.valueOf(indicator.getOrDefault("formula", ""));
            String alias = String.valueOf(indicator.getOrDefault("alias", ""));
            indicator.put("formula", !formula.isEmpty() ? formula : alias);
            indicator.remove("alias");

            // 5. 根据 mType 补充专属字段 & 归入对应分组
            switch (mType) {
                case "range":
                    String code = String.valueOf(rawKProps.get("编码"));
                    Map matched = tagCodeMap.get(code);
                    indicator.put("baseValue", matched != null ? matched.get("standard_value") : null);
                    indicator.put("deviationThreshold", matched != null ? matched.get("range_threshold") : null);
                    groupedResult.get("range").add(indicator);
                    break;
                case "factor":
                    for (Map.Entry<String, String> entry : mPropsFactorKeepMap.entrySet()) {
                        Object val = rawMProps.get(entry.getKey());
                        indicator.put(entry.getValue(), val != null ? val.toString() : "");
                    }
                    groupedResult.get("factor").add(indicator);
                    break;
                default:
                    groupedResult.get("target").add(indicator);
                    break;
            }
        }

// groupedResult 即为最终传给 LLM 的结构化数据
        return buildBenchmarkPrompt(groupedResult, last5BenchmarkRecord);
    }

    /**
     * 将 groupedResult 转换为 LLM 可理解的 Markdown 文本 (Java 8)
     */
    public String buildBenchmarkPrompt(Map<String, List<Map<String, Object>>> groupedResult, List<Map<String, Object>> last5BenchmarkRecord) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 机组标杆指标体系数据\n\n");
        sb.append("以下数据已按业务角色分类，\"-\" 表示该字段未配置。\n\n");

        // 1. 目标指标
        List<Map<String, Object>> targets = groupedResult.get("target");
        if (targets != null && !targets.isEmpty()) {
            sb.append("### 目标标杆 (Target)\n");
            sb.append("| 指标名称 | 描述 | 单位 | 公式/别名 |\n");
            sb.append("|---------|------|------|----------|\n");
            for (Map<String, Object> ind : targets) {
                sb.append(String.format("| %s | %s | %s | %s |\n",
                        safeStr(ind.get("name")),
                        safeStr(ind.get("description")),
                        safeStr(ind.get("unit")),
                        safeStr(ind.get("formula"))));
            }
            sb.append("\n");
        }

        // 2. 范围/基准指标
        List<Map<String, Object>> ranges = groupedResult.get("range");
        if (ranges != null && !ranges.isEmpty()) {
            sb.append("### 范围/基准指标 (Range)\n");
            sb.append("| 指标名称 | 基准值 | 偏差阈值 | 单位 | 描述 |\n");
            sb.append("|---------|--------|---------|------|------|\n");
            for (Map<String, Object> ind : ranges) {
                sb.append(String.format("| %s | %s | %s | %s | %s |\n",
                        safeStr(ind.get("name")),
                        safeNum(ind.get("baseValue")),
                        safeNum(ind.get("deviationThreshold")),
                        safeStr(ind.get("unit")),
                        safeStr(ind.get("description"))));
            }
            sb.append("\n");
        }

        // 3. 影响因素指标
        List<Map<String, Object>> factors = groupedResult.get("factor");
        if (factors != null && !factors.isEmpty()) {
            sb.append("### 影响因素指标 (Factor)\n");
            sb.append("| 指标名称 | 有效性方向 | 重要性排序 | 单位 | 公式/别名 |\n");
            sb.append("|---------|-----------|-----------|------|----------|\n");
            for (Map<String, Object> ind : factors) {
                sb.append(String.format("| %s | %s | %s | %s | %s |\n",
                        safeStr(ind.get("name")),
                        safeStr(ind.get("validityDirection")),
                        safeNum(ind.get("importanceRank")),

                        safeStr(ind.get("unit")),
                        safeStr(ind.get("formula"))));
            }
            sb.append("\n");
        }

        // 4. 生成标杆
        if (last5BenchmarkRecord != null && !last5BenchmarkRecord.isEmpty()) {
            sb.append("### 已生成标杆 (Benchmark，最多仅返回最近5条，如果为空则说明没有标杆)\n");
            sb.append("| 标杆名称 | 标杆创建时间 | 标杆最后更新时间 |\n");
            sb.append("|---------|-----------|-----------|\n");
            for (Map<String, Object> record : last5BenchmarkRecord) {
                sb.append(String.format("| %s | %s | %s |\n",
                        safeStr(record.get("name")),
                        safeStr(record.get("create_time")),
                        safeNum(record.get("update_time"))));
            }
            sb.append("\n");
        } else {
            sb.append("该模型还未生成任何标杆");
            sb.append("\n");
        }

        return sb.toString();
    }

    // Java 8 安全的字符串取值，null/空串统一转为 "-"
    private String safeStr(Object val) {
        if (val == null) return "-";
        String str = val.toString().trim();
        return str.isEmpty() ? "-" : str;
    }

    // Java 8 安全的数值取值，null 转为 "-"
    private String safeNum(Object val) {
        if (val == null) return "-";
        return val.toString();
    }

    public Map getEvaluationByTagCode(String tagCode, Date startDate, Date endDate) {
        Map map = benchmarkMapper.getEvaluationIdByTagCode(tagCode, startDate, endDate);
        Map reMap = new HashMap();
        if (MapUtils.isEmpty(map)) {
            return reMap;
        }

        Integer evaluationId = Integer.valueOf(((Long) map.get("id")).toString());
        Map<String, Map<String, Object>> benchmarkFactors = benchmarkMapper.getBenchmarkFactorByEvaluationId(evaluationId).stream().collect(Collectors.toMap(d -> d.get("model_id").toString(), a -> a, (a1, a2) -> a1));
        Map<String, Map<String, Object>> benchmarkRanges = benchmarkMapper.getBenchmarkRangeByEvaluationId(evaluationId).stream().collect(Collectors.toMap(d -> d.get("model_id").toString(), a -> a, (a1, a2) -> a1));
        Map<String, Map<String, Object>> benchmarkTargets = benchmarkMapper.getBenchmarkTargetByEvaluationId(evaluationId).stream().collect(Collectors.toMap(d -> d.get("model_id").toString(), a -> a, (a1, a2) -> a1));
        List<Map<String, Object>> objectTargets = benchmarkMapper.getObjectTargetByEvaluationId(evaluationId);
        List<Map<String, Object>> objectRanges = benchmarkMapper.getObjectRangeByEvaluationId(evaluationId);
        List<Map<String, Object>> objectFactors = benchmarkMapper.getObjectFactorByEvaluationId(evaluationId);
        objectTargets.forEach(d -> {
            Map<String, Object> benchmarkTarget = benchmarkTargets.get(d.get("model_id").toString());
            Double value = Double.valueOf(d.get("standard_value").toString());
            Double valueBenchmark = Double.valueOf(benchmarkTarget.get("standard_value").toString());
            d.put("benchmark_value", valueBenchmark);
            d.put("difference", Math.abs(valueBenchmark - value));
        });
        reMap.put("目标", objectTargets);
        objectRanges.forEach(d -> {
            Map<String, Object> benchmarkRange = benchmarkRanges.get(d.get("model_id").toString());
            Double value = Double.valueOf(d.get("standard_value").toString());
            Double valueBenchmark = Double.valueOf(benchmarkRange.get("standard_value").toString());
            d.put("benchmark_value", valueBenchmark);
            d.put("difference", Math.abs(valueBenchmark - value));
        });
        reMap.put("范围", objectRanges);
        objectFactors.forEach(d -> {
            Map<String, Object> benchmarkFactor = benchmarkFactors.get(d.get("model_id").toString());
            Double value = Double.valueOf(d.get("standard_value").toString());
            Double valueBenchmark = Double.valueOf(benchmarkFactor.get("standard_value").toString());
            d.put("benchmark_value", valueBenchmark);
            d.put("difference", Math.abs(valueBenchmark - value));
        });
        reMap.put("因素", objectFactors);
        return reMap;
    }
    Pattern VAR_PATTERN = Pattern.compile("\"([^\"]+)\"");
    public String getIndicatorByName(String indicatorName) {
        // 缓存已查询的节点，避免重复查库
        Map<String, Map<String, Object>> cache = new HashMap<>();
        // 记录当前递归路径，防止循环引用
        Set<String> visiting = new HashSet<>();

        final StringBuilder promptBuilder = new StringBuilder();

        promptBuilder.append("以下是指标【").append(indicatorName).append("】的完整计算公式分解树：\n\n");

        // Java 8 不支持局部变量直接递归，通过匿名内部类/函数式接口模拟单方法内的递归能力
        RecursiveResolver resolver = new RecursiveResolver(cache, visiting, promptBuilder);
        resolver.resolve(indicatorName, 0);

        return promptBuilder.toString();
    }

    /**
     * 内部递归解析器，封装所有子逻辑，不对外暴露
     */
    private class RecursiveResolver {
        private final Map<String, Map<String, Object>> cache;
        private final Set<String> visiting;
        private final StringBuilder sb;

        RecursiveResolver(Map<String, Map<String, Object>> cache,
                          Set<String> visiting,
                          StringBuilder sb) {
            this.cache = cache;
            this.visiting = visiting;
            this.sb = sb;
        }

        void resolve(String name, int depth) {
            String indent = repeat("  ", depth);

            // ========== 1. 防环检测 ==========
            if (visiting.contains(name)) {
                sb.append(indent).append("- ").append(name).append(" [CIRCULAR REFERENCE]\n");
                return;
            }

            // ========== 2. 查询节点（带缓存）==========
            Map<String, Object> nodeInfo = cache.computeIfAbsent(name, n -> {
                try {
                    List<Map<String, Object>> results = damExtClient.getIndicatorByBaseName(n, 1);
                    return (results != null && !results.isEmpty()) ? results.get(0) : null;
                } catch (Exception e) {
                    // Feign调用异常时返回null，避免中断整个链路解析
                    System.err.println("Feign query failed for [" + n + "]: " + e.getMessage());
                    return null;
                }
            });

            // ========== 3. 节点不存在处理 ==========
            if (nodeInfo == null) {
                sb.append(indent).append("- ").append(name).append(" [NOT_FOUND - 可能是基础采集值]\n");
                return;
            }

            // ========== 4. 提取公式与子变量 ==========
            Object formulaObj = nodeInfo.get("formula");
            String formula = (formulaObj != null) ? formulaObj.toString() : "";

            sb.append(indent).append("- ").append(name).append("\n");
            sb.append(indent).append("  公式: ").append(formula).append("\n");

            // 无公式或公式为空 → 叶子节点，停止递归
            if (formula.isEmpty()) {
                return;
            }

            // 正则提取所有双引号包裹的子变量（去重且保持顺序）
            List<String> childVars = new ArrayList<>();
            Matcher matcher = VAR_PATTERN.matcher(formula);
            while (matcher.find()) {
                String var = matcher.group(1);
                if (!childVars.contains(var)) {
                    childVars.add(var);
                }
            }

            // ========== 5. 递归解析子变量 ==========
            if (!childVars.isEmpty()) {
                visiting.add(name);
                for (String childVar : childVars) {
                    resolve(childVar, depth + 1);
                }
                visiting.remove(name);
            }
        }

        /** Java 8 兼容的字符串重复方法 */
        private String repeat(String str, int count) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < count; i++) {
                sb.append(str);
            }
            return sb.toString();
        }
    }


}
    