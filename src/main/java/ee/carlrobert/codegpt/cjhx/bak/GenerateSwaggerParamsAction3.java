//package ee.carlrobert.codegpt.cjhx.bak;
//
//import com.intellij.openapi.actionSystem.AnAction;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.actionSystem.CommonDataKeys;
//import com.intellij.openapi.editor.Editor;
//import com.intellij.openapi.ide.CopyPasteManager;
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
//import java.awt.datatransfer.StringSelection;
//import java.util.ArrayList;
//import java.util.List;
//
//public class GenerateSwaggerParamsAction3 extends AnAction {
//
//    private static final Logger log = LoggerFactory.getLogger(GenerateSwaggerParamsAction3.class);
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
//            StringBuilder swaggerAnnotation = new StringBuilder();
//            swaggerAnnotation.append(
//                    /*"import com.monitor.api.ApiInfo;\n" +
//                    "import com.monitor.api.ApiParam;\n" +
//                    "import com.monitor.api.ApiRes;\n" +
//                    "import com.monitor.controller.BaseRestController;\n" +
//                    "import com.system.comm.model.Page;\n" +
//                    "import com.system.handle.model.ResponseCode;\n" +
//                    "import com.system.handle.model.ResponseFrame;\n\n" +*/
//                    "@ApiInfo(params = {\n"
//            );
//
//            PsiParameter[] parameters = containingMethod.getParameterList().getParameters();
//            List<String> paramLines = new ArrayList<>();
//
//            for (PsiParameter parameter : parameters) {
//                PsiType type = parameter.getType();
//                String typeName = type.getCanonicalText();
//
//                // 判断是否是基本类型或常用包装类型
//                if (isBasicType(typeName)) {
//                    // 直接处理基本类型参数
//                    paramLines.add(String.format(
//                            "    @ApiParam(name=\"%s\", code=\"%s\", value=\"\", dataType=\"%s\", required=true)",
//                            parameter.getName(),
//                            parameter.getName(),
//                            typeName
//                    ));
//                } else {
//                    // 处理对象类型，获取其字段
//                    PsiClass psiClass = PsiUtil.resolveClassInType(type);
//                    if (psiClass != null) {
//                        for (PsiField field : psiClass.getAllFields()) {
//                            String fieldName = field.getName();
//                            String fieldType = field.getType().getPresentableText();
//                            String comment = getCleanFieldComment(field);
//
//                            paramLines.add(String.format(
//                                            "    @ApiParam(name=\"%s\", code=\"%s\", value=\"\", dataType=\"%s\", required=true)",
//                                    comment.isEmpty() ? fieldName : comment.replaceAll("\n"," "),
//                                    fieldName,
//                                    fieldType
//                            ));
//                        }
//                    }
//                }
//            }
//
//            swaggerAnnotation.append(String.join(",\n", paramLines));
//            swaggerAnnotation.append("\n}, response = {\n");
//            swaggerAnnotation.append("    @ApiRes(name=\"响应码[0成功/-1失败]\", code=\"code\", clazz=String.class, value=\"0\"),\n");
//            swaggerAnnotation.append("    @ApiRes(name=\"响应消息\", code=\"message\", clazz=String.class, value=\"success\"),\n");
//            swaggerAnnotation.append("    @ApiRes(name=\"主体内容\", code=\"body\", clazz=Object.class, value=\"\")\n");
//            swaggerAnnotation.append("})");
//
//            // 将生成的Swagger注解复制到剪贴板
//            CopyPasteManager.getInstance().setContents(new StringSelection(swaggerAnnotation.toString()));
//
//            // 显示成功消息
//            Messages.showInfoMessage(project, "Swagger注解已生成并复制到剪贴板", "成功");
//        }
//    }
//
//    // 判断是否是基本类型或常用包装类型
//    private boolean isBasicType(String typeName) {
//        return typeName.equals("byte") || typeName.equals("short") || typeName.equals("int") ||
//                typeName.equals("long") || typeName.equals("float") || typeName.equals("double") ||
//                typeName.equals("char") || typeName.equals("boolean") ||
//                typeName.equals("java.lang.Byte") || typeName.equals("java.lang.Short") ||
//                typeName.equals("java.lang.Integer") || typeName.equals("java.lang.Long") ||
//                typeName.equals("java.lang.Float") || typeName.equals("java.lang.Double") ||
//                typeName.equals("java.lang.Character") || typeName.equals("java.lang.Boolean") ||
//                typeName.equals("java.lang.String") || typeName.equals("java.util.Date");
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