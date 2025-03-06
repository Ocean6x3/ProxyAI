package ee.carlrobert.codegpt;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
//帮我写一个 java 冒泡排序代码
public class GenerateMethodByCommentActionBak2 extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);

        if (project == null || editor == null || file == null) {
            return;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(offset);
        // 打印注释信息
        PsiElement comment = PsiTreeUtil.getPrevSiblingOfType(element, PsiComment.class);

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
                    insertTextToEditor(project, editor, offset, answerBuilder.toString());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String extractAnswerFromJson(String jsonLine) {
        System.out.print("______________________1" + jsonLine);
        try {
            if (jsonLine == null || jsonLine.trim().isEmpty()) {
                return null;
            }

            // 提取 JSON 部分（去掉 "data: " 前缀）
            if (jsonLine.startsWith("data: ")) {
                jsonLine = jsonLine.substring(6); // 去掉 "data: "（长度为 6）
            }

            JsonObject jsonObject = JsonParser.parseString(jsonLine).getAsJsonObject();
            return jsonObject.has("answer") ? jsonObject.get("answer").getAsString() : null;
        } catch (Exception e) {
            // 打印异常信息（可选）
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
}

