package ee.carlrobert.codegpt.cjhx;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class main {
    public static void main(String[] args) {
        redisTest();
    }
    public static void test1() {
        String query = "帮我写一个java冒泡排序代码";
        String requestBody = "{"
                + "\"inputs\": {},"
                + "\"query\": \"" + query + "\","
                + "\"response_mode\": \"streaming\","
                + "\"user\": \"apiuser\""
                + "}";
        RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/json; charset=utf-8"));

        OkHttpClient client = new OkHttpClient();
//        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
//        String json = "{\"query\": \"Generate java bubble sort code\"}";
//        RequestBody body = RequestBody.create(mediaType, json);


        try {
            // 调用 Dify API 并逐步处理流式响应

            Request request = new Request.Builder()
                    .url("http://localhost/v1/chat-messages") // 确保 URL 正确
                    .post(body)
                    .addHeader("Authorization", "Bearer app-Jp0N9k9Q6D9clwuNzxAa8Ehi") // 确保 API Key 正确
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response.code());
                }

                try (BufferedReader reader = new BufferedReader(response.body().charStream())) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // 提取 JSON 中的 answer 字段
                        String answer = extractAnswerFromJson(line);
                        System.out.println(answer);
                        String tempAnswerStr = new String(answer);
                        System.out.println("---------------222222"+tempAnswerStr);
                        tempAnswerStr = tempAnswerStr.trim().replace("\r","").replace("\n","");
                        if(tempAnswerStr.contains("```")){
                            answer = answer.replace("```java","").replace("```","");
                        }
                        if(isStartWithChinese(tempAnswerStr) && (tempAnswerStr.startsWith("\n") || tempAnswerStr.startsWith("\r") || tempAnswerStr.startsWith("\t"))){
                            answer = "//"+answer;
                        }

                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }


    public static String extractAnswerFromJson(String jsonLine) {
        try {
            System.out.println("______________________1" + jsonLine);

            if (jsonLine == null || jsonLine.trim().isEmpty()) {
                return null;
            }

            // 提取 JSON 部分（去掉 "data: " 前缀）
            if (jsonLine.startsWith("data: ")) {
                jsonLine = jsonLine.substring(6); // 去掉 "data: "（长度为 6）
            }


            JsonObject jsonObject = JsonParser.parseString(jsonLine).getAsJsonObject();
            String answer = jsonObject.has("answer") ? jsonObject.get("answer").getAsString() : null;
            System.out.println("______________________answer" + answer);
            return answer;
        } catch (Exception e) {
            System.err.println("Failed to parse JSON: " + e.getMessage());
            return null;
        }
    }


    public static void test2(){
        // 调用 Dify API 并实时接收响应
        String apiKey = "app-Jp0N9k9Q6D9clwuNzxAa8Ehi"; // 替换为实际的 API Key
        String url = "http://localhost/v1/chat-messages"; // 替换为实际的 API URL
        String query = "生成java冒泡排序代码"; // 示例查询
        //query = comment.getText();


        // 构建请求体
        String requestBody = "{"
                + "\"inputs\": {},"
                + "\"query\": \"" + query + "\","
                + "\"response_mode\": \"streaming\","
                + "\"user\": \"apiuser\""
                + "}";

        // 发起请求并实时读取响应
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // 获取响应体并逐步读取
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                // 使用缓冲区逐步读取数据
                try (BufferedReader reader = new BufferedReader(responseBody.charStream())) {
                    StringBuilder answerBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // 解析 JSON 数据，提取 answer 字段
                        String answer = extractAnswerFromJson(line);
                        if (answer != null) {
                            answerBuilder.append(answer);
                        }
                    }

                    // 将完整回答插入到编辑器中
                    System.out.println(answerBuilder.toString());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * 判断字符是否为中文字符
     * @param c 输入的字符
     * @return 如果字符是中文字符返回true，否则返回false
     */
    public static boolean isChinese(char c) {
        // 中文字符的Unicode范围
        return (c >= 0x4E00 && c <= 0x9FA5) ||
                (c >= 0x3400 && c <= 0x4DBF) ||
                (c >= 0x20000 && c <= 0x2A6DF) ||
                (c >= 0x2A700 && c <= 0x2B73F) ||
                (c >= 0x2B740 && c <= 0x2B81F) ||
                (c >= 0x2B820 && c <= 0x2CEAF) ||
                (c >= 0xF900 && c <= 0xFAFF) ||
                (c >= 0x2F800 && c <= 0x2FA1F);
    }

    /**
     * 判断字符串是否以中文字符开头
     * @param str 输入的字符串
     * @return 如果字符串以中文字符开头返回true，否则返回false
     */
    public static boolean isStartWithChinese(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        // 获取字符串的第一个字符
        char firstChar = str.charAt(0);

        // 检查第一个字符是否在中文字符的Unicode范围内
        return isChinese(firstChar);
    }



    public static void redisTest() {
        // 创建连接池配置
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128); // 最大连接数
        poolConfig.setMaxIdle(128);  // 最大空闲连接数
        poolConfig.setMinIdle(16);   // 最小空闲连接数

        // 创建连接池
        JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379);

        // 获取 Jedis 连接
        try (Jedis jedis = jedisPool.getResource()) {

            // 设置 apiUrl 和 apiKey
            jedis.set("CODE_GPT_DIFY_API_URL", "http://localhost/v1/chat-messages");
            jedis.set("CODE_GPT_DIFY_API_KEY", "app-Jp0N9k9Q6D9clwuNzxAa8Ehi");


            // 获取 apiUrl 和 apiKey
            String apiUrl = jedis.get("CODE_GPT_DIFY_API_URL");
            String apiKey = jedis.get("CODE_GPT_DIFY_API_KEY");

            // 输出获取到的值
            System.out.println("API URL: " + apiUrl);
            System.out.println("API Key: " + apiKey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接池
            jedisPool.close();
        }
    }
}
