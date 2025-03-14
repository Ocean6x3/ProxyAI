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
        String query = "����дһ��javað���������";
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
            // ���� Dify API ���𲽴�����ʽ��Ӧ

            Request request = new Request.Builder()
                    .url("http://localhost/v1/chat-messages") // ȷ�� URL ��ȷ
                    .post(body)
                    .addHeader("Authorization", "Bearer app-Jp0N9k9Q6D9clwuNzxAa8Ehi") // ȷ�� API Key ��ȷ
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response.code());
                }

                try (BufferedReader reader = new BufferedReader(response.body().charStream())) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // ��ȡ JSON �е� answer �ֶ�
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

            // ��ȡ JSON ���֣�ȥ�� "data: " ǰ׺��
            if (jsonLine.startsWith("data: ")) {
                jsonLine = jsonLine.substring(6); // ȥ�� "data: "������Ϊ 6��
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
        // ���� Dify API ��ʵʱ������Ӧ
        String apiKey = "app-Jp0N9k9Q6D9clwuNzxAa8Ehi"; // �滻Ϊʵ�ʵ� API Key
        String url = "http://localhost/v1/chat-messages"; // �滻Ϊʵ�ʵ� API URL
        String query = "����javað���������"; // ʾ����ѯ
        //query = comment.getText();


        // ����������
        String requestBody = "{"
                + "\"inputs\": {},"
                + "\"query\": \"" + query + "\","
                + "\"response_mode\": \"streaming\","
                + "\"user\": \"apiuser\""
                + "}";

        // ��������ʵʱ��ȡ��Ӧ
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

            // ��ȡ��Ӧ�岢�𲽶�ȡ
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                // ʹ�û������𲽶�ȡ����
                try (BufferedReader reader = new BufferedReader(responseBody.charStream())) {
                    StringBuilder answerBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // ���� JSON ���ݣ���ȡ answer �ֶ�
                        String answer = extractAnswerFromJson(line);
                        if (answer != null) {
                            answerBuilder.append(answer);
                        }
                    }

                    // �������ش���뵽�༭����
                    System.out.println(answerBuilder.toString());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * �ж��ַ��Ƿ�Ϊ�����ַ�
     * @param c ������ַ�
     * @return ����ַ��������ַ�����true�����򷵻�false
     */
    public static boolean isChinese(char c) {
        // �����ַ���Unicode��Χ
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
     * �ж��ַ����Ƿ��������ַ���ͷ
     * @param str ������ַ���
     * @return ����ַ����������ַ���ͷ����true�����򷵻�false
     */
    public static boolean isStartWithChinese(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        // ��ȡ�ַ����ĵ�һ���ַ�
        char firstChar = str.charAt(0);

        // ����һ���ַ��Ƿ��������ַ���Unicode��Χ��
        return isChinese(firstChar);
    }



    public static void redisTest() {
        // �������ӳ�����
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128); // ���������
        poolConfig.setMaxIdle(128);  // ������������
        poolConfig.setMinIdle(16);   // ��С����������

        // �������ӳ�
        JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379);

        // ��ȡ Jedis ����
        try (Jedis jedis = jedisPool.getResource()) {

            // ���� apiUrl �� apiKey
            jedis.set("CODE_GPT_DIFY_API_URL", "http://localhost/v1/chat-messages");
            jedis.set("CODE_GPT_DIFY_API_KEY", "app-Jp0N9k9Q6D9clwuNzxAa8Ehi");


            // ��ȡ apiUrl �� apiKey
            String apiUrl = jedis.get("CODE_GPT_DIFY_API_URL");
            String apiKey = jedis.get("CODE_GPT_DIFY_API_KEY");

            // �����ȡ����ֵ
            System.out.println("API URL: " + apiUrl);
            System.out.println("API Key: " + apiKey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // �ر����ӳ�
            jedisPool.close();
        }
    }
}
