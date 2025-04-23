/*
package ee.carlrobert.codegpt.cjhx.bak;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import ee.carlrobert.codegpt.cjhx.utils.RedisUtil;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GenerateMethodByCommentAction extends AnAction {
    private static final Logger log = LoggerFactory.getLogger(GenerateMethodByCommentAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);

        if (project == null || editor == null || file == null) {
            return;
        }

        int initialOffset = editor.getCaretModel().getOffset();
        AtomicInteger offset = new AtomicInteger(initialOffset);
        // 打印注释信息
        PsiElement element = file.findElementAt(initialOffset);
        PsiElement comment = PsiTreeUtil.getPrevSiblingOfType(element, PsiComment.class);
        String query = comment.getText();
        if(StringUtil.isEmpty(query)){
            return;
        }else{
            query += "不需要 public class 类代码，只需要方法代码，尽可能洁简的回答，java代码";
        }


        */
/* dev *//*

//        String apiUrl = "http://localhost/v1/chat-messages";
//        String apiKey = "app-Jp0N9k9Q6D9clwuNzxAa8Ehi";

        */
/* prod *//*

        */
/*String apiUrl = "http://10.191.0.140/v1/chat-messages";
        http://10.190.220.33:3000/v1/chat/completions
        String apiKey = "app-ySzj9mAfN5aoEuHc2nlnmgq6";*//*

        String keyAndUrl = RedisUtil.getKeyAndUrl();
        String apiUrl = keyAndUrl.split(",")[0];
        String apiKey = keyAndUrl.split(",")[1];
        */
/*直接请求Dify*//*

        String requestBody = "{"
                + "\"inputs\": {},"
                + "\"query\": \"" + query + "\","
                + "\"response_mode\": \"streaming\","
                + "\"user\": \"apiuser\""
                + "}";
        */
/* 通过代理请求Dify *//*

        */
/*String requestBody = "{\n" +
                "  \"model\": \"gpt-4\",\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": \""+query+"\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"temperature\": 0.1,\n" +
                "  \"stream\": true,\n" +
                "  \"max_tokens\": 9999\n" +
                "}";*//*

        // 异步执行网络请求和流式响应处理
        CompletableFuture.runAsync(() -> {
            try {
                // 调用 Dify API 并逐步处理流式响应

                RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/json; charset=utf-8"));
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)  // 设置连接超时时间为60秒
                        .readTimeout(120, TimeUnit.SECONDS)    // 设置读取超时时间为60秒
                        .writeTimeout(120, TimeUnit.SECONDS)   // 设置写入超时时间为60秒
                        .build();
                log.info("_____________apiUrlAndApiKey_________"+apiUrl+"_____________________________"+apiKey);

                Request request = new Request.Builder()
                        .url(apiUrl) // 确保 URL 正确
                        .post(body)
                        .addHeader("Authorization", "Bearer "+apiKey) // 确保 API Key 正确
                        .addHeader("Content-Type", "application/json")
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response.code());
                    }

                    try (BufferedReader reader = new BufferedReader(response.body().charStream())) {
                        Boolean first = true;
                        String line;
                        while ((line = reader.readLine()) != null) {
                            // 提取 JSON 中的 answer 字段
                            String answer = extractAnswerFromJson(line);
                            if(first){
                                first = false;
                                answer = "\n\t//---------- Generating code…… at "+formatDate(new Date())+"----------\n\n"+(answer!=null && !StringUtil.isEmpty(answer.trim()) ? answer : "");
                            }
                            if (answer != null) {
                                String tempAnswerStr = new String(answer);
                                tempAnswerStr = tempAnswerStr.trim().replace("\r","").replace("\n","");
                                if (tempAnswerStr.startsWith("当然") ||  tempAnswerStr.startsWith("好的") || tempAnswerStr.startsWith("这个") || tempAnswerStr.startsWith("```")) {
                                    answer = "//"+answer;
                                }
                                */
/*if(tempAnswerStr.contains("```")){
                                    answer = answer.replace("```java","").replace("```","");
                                }*//*

                                if(isStartWithChinese(tempAnswerStr) && (tempAnswerStr.startsWith("\n") || tempAnswerStr.startsWith("\r") || tempAnswerStr.startsWith("\t"))){
                                    answer = "//"+answer;
                                }

                                // 在主线程中插入文本
                                insertTextToEditor(project, editor, offset.get(), answer);
                                offset.addAndGet(answer.length()); // 更新插入位置
                            }
                        }

                        // 在主线程中插入文本
                        String endText = "\n\n\t//---------- End of generated code, at "+formatDate(new Date())+"----------";
                        insertTextToEditor(project, editor, offset.get(), endText);
                        offset.addAndGet(endText.length()); // 更新插入位置
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private String extractAnswerFromJson(String jsonLine) {
        try {
            if (jsonLine == null || jsonLine.trim().isEmpty()) {
                return null;
            }
            // 提取 JSON 部分（去掉 "data: " 前缀）
            if (jsonLine.startsWith("data: ")) {
                jsonLine = jsonLine.substring(6); // 去掉 "data: "（长度为 6）
            }
            JsonObject jsonObject = JsonParser.parseString(jsonLine).getAsJsonObject();
            String answer = jsonObject.has("answer") ? jsonObject.get("answer").getAsString() : null;
            return answer;
        } catch (Exception e) {
            System.err.println("Failed to parse JSON: " + e.getMessage());
            return null;
        }
    }

    private void insertTextToEditor(Project project, Editor editor, int offset, String text) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            Document document = editor.getDocument();
            document.insertString(offset, text);
        });
    }

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"query\": \"Generate java bubble sort code\"}";
        RequestBody requestBody = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url("http://localhost/v1/chat-messages") // 替换为实际的 API URL
                .post(requestBody)
                .addHeader("Authorization", "Bearer app-Jp0N9k9Q6D9clwuNzxAa8Ehi") // 替换为实际的 API Key
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response.code());
            }
            // 处理响应
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    */
/**
     * 判断字符是否为中文字符
     * @param c 输入的字符
     * @return 如果字符是中文字符返回true，否则返回false
     *//*

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

    */
/**
     * 判断字符串是否以中文字符开头
     * @param str 输入的字符串
     * @return 如果字符串以中文字符开头返回true，否则返回false
     *//*

    public static boolean isStartWithChinese(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        // 获取字符串的第一个字符
        char firstChar = str.charAt(0);

        // 检查第一个字符是否在中文字符的Unicode范围内
        return isChinese(firstChar);
    }


    public static String formatDate(Date date){
        // 创建SimpleDateFormat对象，并设置所需的日期时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        // 将Date对象格式化为字符串
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
}






*/
