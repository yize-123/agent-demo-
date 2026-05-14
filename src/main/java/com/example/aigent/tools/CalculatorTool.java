package com.example.aigent.tools;

import com.example.aigent.model.Tool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 计算器工具
 * 支持基本数学运算：加减乘除、幂运算、平方根等
 */
@Slf4j
@Component
public class CalculatorTool implements Tool {

    private final ObjectMapper objectMapper;

    public CalculatorTool(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getName() {
        return "calculator";
    }

    @Override
    public String getDescription() {
        return "执行数学计算。支持的操作：add(加法)、subtract(减法)、multiply(乘法)、divide(除法)、power(幂运算)、sqrt(平方根)。参数：operation(操作类型)、num1(第一个数)、num2(第二个数，除法和幂运算需要)";
    }

    @Override
    public JsonNode getParameters() {
        ObjectNode params = objectMapper.createObjectNode();
        params.put("type", "object");
        
        ObjectNode properties = objectMapper.createObjectNode();
        
        // 操作类型
        ObjectNode operationParam = objectMapper.createObjectNode();
        operationParam.put("type", "string");
        operationParam.put("description", "操作类型：add、subtract、multiply、divide、power、sqrt");
        properties.set("operation", operationParam);
        
        // 第一个数
        ObjectNode num1Param = objectMapper.createObjectNode();
        num1Param.put("type", "number");
        num1Param.put("description", "第一个操作数");
        properties.set("num1", num1Param);
        
        // 第二个数（可选）
        ObjectNode num2Param = objectMapper.createObjectNode();
        num2Param.put("type", "number");
        num2Param.put("description", "第二个操作数（除法和幂运算需要）");
        properties.set("num2", num2Param);
        
        params.set("properties", properties);
        params.putArray("required").add("operation").add("num1");
        
        return params;
    }

    @Override
    public String execute(JsonNode arguments) {
        try {
            String operation = arguments.get("operation").asText().toLowerCase();
            BigDecimal num1 = BigDecimal.valueOf(arguments.get("num1").asDouble());
            BigDecimal num2 = arguments.has("num2") ? BigDecimal.valueOf(arguments.get("num2").asDouble()) : null;
            
            log.info("执行计算: {} {} {}", num1, operation, num2);
            
            BigDecimal result;
            switch (operation) {
                case "add":
                    result = num1.add(num2 != null ? num2 : BigDecimal.ZERO);
                    break;
                case "subtract":
                    result = num1.subtract(num2 != null ? num2 : BigDecimal.ZERO);
                    break;
                case "multiply":
                    result = num1.multiply(num2 != null ? num2 : BigDecimal.ONE);
                    break;
                case "divide":
                    if (num2 == null || num2.compareTo(BigDecimal.ZERO) == 0) {
                        return "{\"error\": \"除数不能为零\"}";
                    }
                    result = num1.divide(num2, 10, RoundingMode.HALF_UP);
                    break;
                case "power":
                    if (num2 == null) {
                        return "{\"error\": \"幂运算需要第二个参数\"}";
                    }
                    result = num1.pow(num2.intValue());
                    break;
                case "sqrt":
                    if (num1.compareTo(BigDecimal.ZERO) < 0) {
                        return "{\"error\": \"不能对负数取平方根\"}";
                    }
                    result = BigDecimal.valueOf(Math.sqrt(num1.doubleValue()));
                    break;
                default:
                    return "{\"error\": \"未知操作类型: " + operation + "\"}";
            }
            
            return "{\"operation\": \"" + operation + "\", \"num1\": " + num1 + 
                   (num2 != null ? ", \"num2\": " + num2 : "") + 
                   ", \"result\": " + result + "}";
                   
        } catch (Exception e) {
            log.error("计算失败", e);
            return "{\"error\": \"计算失败: " + e.getMessage() + "\"}";
        }
    }
}
