package com.example.aigent.tools;

import com.example.aigent.model.Tool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

/**
 * 时间查询工具
 * 提供当前时间、日期计算等功能
 */
@Slf4j
@Component
public class TimeTool implements Tool {

    private final ObjectMapper objectMapper;

    public TimeTool(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getName() {
        return "get_time";
    }

    @Override
    public String getDescription() {
        return "获取当前时间或计算日期。支持的操作：current(当前时间)、add_days(添加天数)、diff_days(计算日期差)。参数：operation(操作类型)、date(日期，格式：yyyy-MM-dd，可选)、days(天数，可选)";
    }

    @Override
    public JsonNode getParameters() {
        ObjectNode params = objectMapper.createObjectNode();
        params.put("type", "object");
        
        ObjectNode properties = objectMapper.createObjectNode();
        
        ObjectNode operationParam = objectMapper.createObjectNode();
        operationParam.put("type", "string");
        operationParam.put("description", "操作类型：current、add_days、diff_days");
        properties.set("operation", operationParam);
        
        ObjectNode dateParam = objectMapper.createObjectNode();
        dateParam.put("type", "string");
        dateParam.put("description", "日期，格式：yyyy-MM-dd");
        properties.set("date", dateParam);
        
        ObjectNode daysParam = objectMapper.createObjectNode();
        daysParam.put("type", "integer");
        daysParam.put("description", "天数");
        properties.set("days", daysParam);
        
        params.set("properties", properties);
        params.putArray("required").add("operation");
        
        return params;
    }

    @Override
    public String execute(JsonNode arguments) {
        try {
            String operation = arguments.get("operation").asText().toLowerCase();
            
            log.info("执行时间操作: {}", operation);
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            switch (operation) {
                case "current":
                    LocalDateTime now = LocalDateTime.now();
                    LocalDate today = LocalDate.now();
                    DayOfWeek dayOfWeek = today.getDayOfWeek();
                    
                    String weekDay = switch (dayOfWeek) {
                        case MONDAY -> "星期一";
                        case TUESDAY -> "星期二";
                        case WEDNESDAY -> "星期三";
                        case THURSDAY -> "星期四";
                        case FRIDAY -> "星期五";
                        case SATURDAY -> "星期六";
                        case SUNDAY -> "星期日";
                    };
                    
                    return "{\"operation\": \"current\", \"date\": \"" + today.format(dateFormatter) + "\", " +
                           "\"time\": \"" + now.format(timeFormatter) + "\", \"day_of_week\": \"" + weekDay + "\"}";
                           
                case "add_days":
                    if (!arguments.has("date")) {
                        return "{\"error\": \"需要提供 date 参数\"}";
                    }
                    if (!arguments.has("days")) {
                        return "{\"error\": \"需要提供 days 参数\"}";
                    }
                    
                    LocalDate date = LocalDate.parse(arguments.get("date").asText(), dateFormatter);
                    int days = arguments.get("days").asInt();
                    LocalDate resultDate = date.plusDays(days);
                    
                    return "{\"operation\": \"add_days\", \"original_date\": \"" + date.format(dateFormatter) + "\", " +
                           "\"days\": " + days + ", \"result_date\": \"" + resultDate.format(dateFormatter) + "\"}";
                           
                case "diff_days":
                    if (!arguments.has("date")) {
                        return "{\"error\": \"需要提供 date 参数\"}";
                    }
                    
                    LocalDate date = LocalDate.parse(arguments.get("date").asText(), dateFormatter);
                    LocalDate today = LocalDate.now();
                    long daysBetween = ChronoUnit.DAYS.between(today, date);
                    
                    String relation = daysBetween > 0 ? "之后" : daysBetween < 0 ? "之前" : "今天";
                    
                    return "{\"operation\": \"diff_days\", \"target_date\": \"" + date.format(dateFormatter) + "\", " +
                           "\"days_between\": " + Math.abs(daysBetween) + ", \"relation\": \"" + relation + "\"}";
                           
                default:
                    return "{\"error\": \"未知操作类型: " + operation + "\"}";
            }
            
        } catch (Exception e) {
            log.error("时间操作失败", e);
            return "{\"error\": \"时间操作失败: " + e.getMessage() + "\"}";
        }
    }
}
