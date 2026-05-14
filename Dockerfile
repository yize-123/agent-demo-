# AI Agent Demo Dockerfile
# 使用Java 17作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制Maven依赖文件
COPY pom.xml .

# 安装Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# 下载依赖（利用Docker缓存）
RUN mvn dependency:go-offline -q

# 复制源代码
COPY src ./src

# 编译项目
RUN mvn clean package -DskipTests -q

# 复制运行时配置
COPY src/main/resources/application.yml /app/config/

# 暴露端口
EXPOSE 8080

# 运行命令
ENTRYPOINT ["java", "-jar", "target/ai-agent-demo-1.0.0.jar", "--spring.config.location=file:/app/config/application.yml"]
