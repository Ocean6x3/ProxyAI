package ee.carlrobert.codegpt.cjhx.utils;

import ee.carlrobert.codegpt.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {
//    static String env = "dev";
    static String env = "prod";

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
        String apiUrl = "";
        String apiKey = "";
        String redisHost = "localhost";
        if(!"dev".equals(env)) {
            redisHost = "10.190.220.33";
        }

        try (Jedis jedis = new Jedis(redisHost, 6379)) {
            // ��ȡ apiUrl �� apiKey
            apiUrl = jedis.get("CODE_GPT_DIFY_API_URL");
            apiKey = jedis.get("CODE_GPT_DIFY_API_KEY");
        } catch (Exception e) {
            e.printStackTrace();
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
        return apiUrl+","+apiKey;
    }
}
