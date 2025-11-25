package com.example.aitemperaturebackend.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*") // 临时允许所有前端访问
public class AIAnalysisController {

    @Value("${zhipu.api-key}")
    private String apiKey;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 分模块调用大模型分析温度数据
     * @param module 模块名: endpoint / edge / cloud / trend / alert
     * @return AI分析文本
     */
    @GetMapping("/analyzeTemperature/{module}")
    public String analyzeTemperatureByModule(@PathVariable String module) {
        try {
            // 1. 查询最近10条温度数据（来自 sensor_data 表）
            String sql = "SELECT temperature FROM sensor_data ORDER BY timestamp DESC LIMIT 10";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

            if (rows.isEmpty()) return "数据库中没有温度数据";

            String tempListStr = rows.stream()
                    .map(r -> r.get("temperature").toString())
                    .collect(Collectors.joining(", "));

            // 2. 根据模块生成不同 prompt
            String prompt = generatePrompt(module, tempListStr);
            if (prompt == null) return "未知模块名: " + module;

            // 3. 构造 JSON 请求体
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);

            JSONObject json = new JSONObject();
            json.put("model", "glm-4-flash");
            json.put("messages", java.util.Arrays.asList(message));

            okhttp3.RequestBody body = okhttp3.RequestBody.create(
                    okhttp3.MediaType.get("application/json; charset=utf-8"),
                    json.toJSONString()
            );


            Request httpRequest = new Request.Builder()
                    .url("https://open.bigmodel.cn/api/paas/v4/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .post(body)
                    .build();

            // 4. 调用智谱大模型
            try (Response response = client.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) return "调用AI接口失败: " + response.code();
                String responseBody = response.body().string();

                // 5. 解析 AI 返回内容
                JSONObject result = JSON.parseObject(responseBody);
                JSONArray choices = result.getJSONArray("choices");
                if (choices != null && choices.size() > 0) {
                    JSONObject messageObj = choices.getJSONObject(0).getJSONObject("message");
                    if (messageObj != null) {
                        String aiText = messageObj.getString("content");
                        return aiText != null ? aiText : "AI返回内容为空";
                    }
                }
                return "AI返回内容为空";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "调用AI接口异常: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "系统异常: " + e.getMessage();
        }
    }

    /**
     * 根据模块生成对应 prompt
     */
    private String generatePrompt(String module, String tempListStr) {
        switch (module.toLowerCase()) {
            case "endpoint":
                return String.format(
                        "当前设备最新10次测温数据为：[%s]。\n请基于提供的温度数据分析端侧换芯进展，包括传感器/变送器的替换或升级情况，精度、通信稳定性、响应时间等指标，用中文生成一句话来描述，大概30字左右，不生成代码。",
                        tempListStr
                );
            case "edge":
                return String.format(
                        "当前设备最新10次测温数据为：[%s]。\n请基于提供的温度数据分析边缘计算状态，包括节点运行稳定性、日均处理数据量、系统迁移/算法优化进度、效率提升情况，用中文生成一句话来描述，大概30字左右，不生成代码。",
                        tempListStr
                );
            case "cloud":
                return String.format(
                        "当前设备最新10次测温数据为：[%s]。\n请基于提供的温度数据分析云端监测概览，包括实时监控设备数量、温度状态、异常识别次数、预测性维护建议生成情况，用中文生成一句话来描述，大概30字左右，不生成代码。",
                        tempListStr
                );
            case "trend":
                return String.format(
                        "当前设备最新10次测温数据为：[%s]。\n请基于提供的温度数据进行温度趋势预测，分析未来24小时设备温度变化，评估异常风险，并给出置信度，用中文生成一句话来描述，大概30字左右，不生成代码。",
                        tempListStr
                );
            case "alert":
                return String.format(
                        "当前设备最新10次测温数据为：[%s]。\n请基于提供的温度数据分析预警与告警情况，包括异常告警次数、推送状态、平均响应时间，用中文生成一句话来描述，大概30字左右，不生成代码。",
                        tempListStr
                );
            default:
                return null;
        }
    }
}
