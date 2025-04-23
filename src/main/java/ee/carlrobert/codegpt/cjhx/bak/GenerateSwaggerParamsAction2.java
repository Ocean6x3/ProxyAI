//package ee.carlrobert.codegpt.cjhx.bak;
//
//import com.intellij.openapi.actionSystem.AnAction;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.actionSystem.CommonDataKeys;
//import com.intellij.openapi.editor.Editor;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.ui.Messages;
//import com.intellij.psi.*;
//import com.intellij.psi.javadoc.PsiDocComment;
//import com.intellij.psi.util.PsiTreeUtil;
//import com.intellij.psi.util.PsiUtil;
//import org.jetbrains.annotations.NotNull;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class GenerateSwaggerParamsAction2 extends AnAction {
//
//    private static final Logger log = LoggerFactory.getLogger(GenerateSwaggerParamsAction2.class);
//
//    @Override
//    public void actionPerformed(@NotNull AnActionEvent event) {
//        Project project = event.getProject();
//        if (project == null) return;
//
//        Editor editor = event.getData(CommonDataKeys.EDITOR);
//        if (editor == null) return;
//
//        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
//        if (psiFile == null) return;
//
//        PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
//        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
//
//        if (containingMethod != null) {
//            StringBuilder combinedInfo = new StringBuilder();
//            PsiParameter[] parameters = containingMethod.getParameterList().getParameters();
//
//            for (PsiParameter parameter : parameters) {
//                PsiType type = parameter.getType();
//                PsiClass psiClass = PsiUtil.resolveClassInType(type);
//
//                if (psiClass != null) {
//                    combinedInfo.append("参数: ").append(parameter.getName())
//                            .append(" (类型: ").append(psiClass.getQualifiedName()).append(")\n\n");
//
//                    // 处理所有字段
//                    for (PsiField field : psiClass.getAllFields()) {
//                        combinedInfo.append(field.getName())
//                                .append(" (").append(field.getType().getPresentableText()).append(")");
//
//                        // 获取并格式化字段注释
//                        String comment = getCleanFieldComment(field);
//                        if (!comment.isEmpty()) {
//                            combinedInfo.append(" - ").append(comment);
//                        }
//                        combinedInfo.append("\n");
//                    }
//                    combinedInfo.append("\n----------------\n");
//                }
//            }
//
//            if (combinedInfo.length() > 0) {
//                log.info("字段分析结果:\n{}", combinedInfo.toString());
//                Messages.showInfoMessage(project, combinedInfo.toString(), "字段信息");
//            }
//        }
//    }
//
//    /**
//     * 获取字段注释（最终可靠版）
//     */
//    /**
//     * 获取字段注释（增强版）
//     */
//    private String getCleanFieldComment(PsiField field) {
////        printAllPrevElements(field);
////        log.info("++++++++++++++++++++++++++++++");
//        // 1. 优先检查字段自带的JavaDoc注释
//        PsiDocComment docComment = field.getDocComment();
//        if (docComment != null) {
//            return cleanJavaDocComment(docComment.getText());
//        }
//
//        // 2. 直接从字段元素中提取注释
//        String fieldText = field.getText();
//        StringBuilder commentBuilder = new StringBuilder();
//
//        // 处理多行注释 /* ... */
//        if (fieldText.contains("/*") && fieldText.contains("*/")) {
//            String comment = fieldText.substring(fieldText.indexOf("/*"), fieldText.indexOf("*/") + 2);
//            commentBuilder.append(comment.replaceAll("/\\*|\\*/", "").trim());
//        }
//        // 处理单行注释 //
//        else if (fieldText.contains("//")) {
//            String comment = fieldText.substring(fieldText.indexOf("//") + 2);
//            // 只取到行尾或字段声明开始处
//            int endIndex = comment.indexOf("private");
//            if (endIndex > 0) {
//                comment = comment.substring(0, endIndex);
//            }
//            commentBuilder.append(comment.trim());
//        }
//        // 处理JavaDoc /** ... */
//        else if (fieldText.contains("/**") && fieldText.contains("*/")) {
//            String comment = fieldText.substring(fieldText.indexOf("/**"), fieldText.indexOf("*/") + 2);
//            commentBuilder.append(cleanJavaDocComment(comment));
//        }
//
//        // 3. 检查注解后的注释
//        PsiModifierList modifierList = field.getModifierList();
//        if (modifierList != null) {
//            for (PsiElement child : modifierList.getChildren()) {
//                if (child instanceof PsiComment) {
//                    String commentText = child.getText();
//                    if (commentText.startsWith("//")) {
//                        if (commentBuilder.length() > 0) {
//                            commentBuilder.append(" ");
//                        }
//                        commentBuilder.append(commentText.substring(2).trim());
//                    }
//                }
//            }
//        }
//
//        return commentBuilder.toString().trim();
//    }
//
//    /**
//     * 获取前一个非空白元素（跳过所有空白）
//     */
//    private PsiElement getPreviousNonWhitespaceElement(PsiElement element) {
//        while (element != null && element instanceof PsiWhiteSpace) {
//            element = element.getPrevSibling();
//        }
//        return element;
//    }
//
//    /**
//     * 清理JavaDoc注释（优化版）
//     */
//    private String cleanJavaDocComment(String text) {
//        return text.replaceAll("/\\*\\*|\\*/", "")  // 去除头尾标记
//                .replaceAll("(?m)^\\s*\\*\\s*", "") // 去除每行星号
//                .replaceAll("@\\w+.*", "")       // 去除标签
//                .replaceAll("\\n\\s*", " ")      // 处理换行
//                .trim();
//    }
//
//
//    private void printAllPrevElements(PsiField field) {
//        System.out.println("=== 完整前驱元素分析 ===");
//        System.out.println("字段: " + field.getName());
//
//        PsiElement prev = field.getPrevSibling();
//        while (prev != null) {
//            System.out.println("元素类型: " + prev.getClass().getSimpleName());
//            System.out.println("元素内容: " + prev.getText());
//            System.out.println("-----");
//            prev = prev.getPrevSibling();
//        }
//
//        PsiModifierList modifiers = field.getModifierList();
//        if (modifiers != null) {
//            System.out.println("=== 修饰符列表 ===");
//            for (PsiElement child : modifiers.getChildren()) {
//                System.out.println("修饰符元素: " + child.getClass().getSimpleName());
//                System.out.println("内容: " + child.getText());
//            }
//        }
//    }
//}