/*
package ee.carlrobert.codegpt;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import ee.carlrobert.codegpt.settings.CustomOpenAISettings;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GenerateMethodByCommentActionBak extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);

        if (project == null || editor == null || file == null) {
            return;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(offset);

        // 这里添加你的逻辑，根据备注生成方法代码
        // 例如，查找包含特定注释的类，并生成相应的方法
        CustomOpenAISettings settings = ServiceManager.getService(project, CustomOpenAISettings.class);

        String apiKey = settings.getApiKey();
        String url = settings.getUrl();
        Map<String, String> headers = settings.getHeaders();

        // 使用这些配置信息来执行您的逻辑
        System.out.println("__________________________");
        System.out.println("API Key: " + apiKey);
        System.out.println("URL: " + url);
        System.out.println("Headers: " + headers);
        // 打印 Project 信息
        System.out.println("Project Name: " + project.getName());
        System.out.println("Project Base Path: " + project.getBasePath());
        System.out.println("Project Base Directory: " + project.getBaseDir());

        // 打印 Editor 和 PsiFile 信息
        System.out.println("Editor: " + editor);
        System.out.println("PsiFile: " + file);
        System.out.println("PsiFile Name: " + file.getName());
        System.out.println("PsiFile Type: " + file.getFileType());

        // 打印注释信息
        PsiElement comment = PsiTreeUtil.getPrevSiblingOfType(element, PsiComment.class);
        String commentText = "";
        if (comment != null) {
            commentText = comment.getText();
            System.out.println("Found comment: " + comment.getText());
            System.out.println("Comment Type: " + comment.getClass().getSimpleName());
        } else {
            System.out.println("No comment found at offset " + offset);
        }

        // 打印 PsiClass 信息
        PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        if (psiClass != null) {
            System.out.println("PsiClass Name: " + psiClass.getName());
            System.out.println("PsiClass Qualified Name: " + psiClass.getQualifiedName());
            System.out.println("PsiClass Containing File: " + psiClass.getContainingFile());
        }

        // 打印配置信息
        System.out.println("API Key: " + settings.getApiKey());
        System.out.println("URL: " + settings.getUrl());
        if (headers != null) {
            System.out.println("Headers:");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
        } else {
            System.out.println("Headers: null");
        }
        System.out.println("__________________________");


        if (psiClass != null) {
            String methodBody = settings.getState()+"\npublic void methodName() {\n" +
                    "    // 1111方法体\n" +
                    "}\n"+commentText+"apiKey="+apiKey+"；url="+url+"；headers="+headers+"；settings="+settings.toString();
            WriteCommandAction.runWriteCommandAction(project, () -> {
                Document document = editor.getDocument();
                CodeStyleManager styleManager = CodeStyleManager.getInstance(project);
                document.insertString(offset, methodBody);
                styleManager.reformat(psiClass);
            });
        }
    }
}
*/
