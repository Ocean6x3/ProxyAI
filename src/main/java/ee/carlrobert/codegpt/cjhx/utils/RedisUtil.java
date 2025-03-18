package ee.carlrobert.codegpt.cjhx.utils;

import ee.carlrobert.codegpt.util.StringUtil;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;

public class RedisUtil {
    private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);
    static final Boolean isProd = true;

    //static JedisPool jedisPool = null;
    /*static {
        // �������ӳ�����
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(30); // ���������
        poolConfig.setMaxIdle(30);  // ������������
        poolConfig.setMinIdle(2);   // ��С����������

        if("dev".equals(env)) {
            System.out.println("_______________2________________");
            // �������ӳ�
            jedisPool = new JedisPool(poolConfig, "localhost", 6379);
        }else {
            System.out.println("_______________3________________");
            // �������ӳ�
            jedisPool = new JedisPool(poolConfig, "10.190.220.33", 6379);
        }
    }
*/
    public static String getKeyAndUrl() {
        String apiUrlAndKey = "";
        String apiUrl = "";
        String apiKey = "";
        String redisHost = "localhost";
        if(isProd) {
            RequestBody body = RequestBody.create("", MediaType.parse("application/json; charset=utf-8"));
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://10.190.220.33:3000/api/getCodeGptKeyAndUrl") // ȷ�� URL ��ȷ
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            try (Response response = client.newCall(request).execute()) {
                apiUrlAndKey = response.body().string();
            } catch (IOException e) {
                //throw new RuntimeException(e);
            }
        }else{
            try (Jedis jedis = new Jedis(redisHost, 6379)) {
                // ��ȡ apiUrl �� apiKey
                apiUrl = jedis.get("CODE_GPT_DIFY_API_URL");
                apiKey = jedis.get("CODE_GPT_DIFY_API_KEY");
                apiUrlAndKey= apiUrl+","+apiKey;
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

    /*
        // ��ȡ Jedis ����
        try (Jedis jedis = jedisPool.getResource()) {

            // ���� apiUrl �� apiKey
//            jedis.set("CODE_GPT_DIFY_API_URL", "http://localhost/v1/chat-messages");
//            jedis.set("CODE_GPT_DIFY_API_KEY", "app-Jp0N9k9Q6D9clwuNzxAa8Ehi");

            // ��ȡ apiUrl �� apiKey
            apiUrl = jedis.get("CODE_GPT_DIFY_API_URL");
            apiKey = jedis.get("CODE_GPT_DIFY_API_KEY");

            System.out.println("API URL: " + apiUrl);
            System.out.println("API Key: " + apiKey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // �ر����ӳ�
            jedisPool.close();
        }
        */
        return apiUrlAndKey;
    }
}
