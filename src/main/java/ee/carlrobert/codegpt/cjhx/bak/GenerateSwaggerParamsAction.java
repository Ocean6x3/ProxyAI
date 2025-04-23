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
//import java.util.ArrayList;
//import java.util.List;
//
//public class GenerateSwaggerParamsAction extends AnAction {
//
//    private static final Logger log = LoggerFactory.getLogger(GenerateSwaggerParamsAction.class);
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
//     * 获取并清理字段注释（修复行注释获取问题）
//     */
//    private String getCleanFieldComment(PsiField field) {
//        // 1. 先尝试获取JavaDoc注释（/** */ 这种）
//        PsiDocComment docComment = field.getDocComment();
//        if (docComment != null) {
//            return cleanJavaDocComment(docComment.getText());
//        }
//
//        // 2. 获取字段前的所有注释（包括行注释）
//        PsiElement prevSibling = field.getPrevSibling();
//        while (prevSibling != null) {
//            // 跳过空白和注解
//            if (prevSibling instanceof PsiWhiteSpace || prevSibling instanceof PsiAnnotation) {
//                prevSibling = prevSibling.getPrevSibling();
//                continue;
//            }
//
//            // 找到最近的注释
//            if (prevSibling instanceof PsiComment) {
//                String commentText = prevSibling.getText();
//                // 处理行注释
//                if (commentText.startsWith("//")) {
//                    return commentText.substring(2).trim();
//                }
//                // 处理块注释 /* */
//                if (commentText.startsWith("/*")) {
//                    return commentText.replaceAll("/\\*|\\*/", "").trim();
//                }
//            }
//            break; // 遇到非注释元素就停止搜索
//        }
//
//        return ""; // 没有找到注释
//    }
//
//    /**
//     * 清理JavaDoc注释
//     */
//    private String cleanJavaDocComment(String text) {
//        return text.replaceAll("/\\*\\*|\\*/", "") // 去除头尾标记
//                .replaceAll("\\*", "")           // 去除每行星号
//                .replaceAll("@\\w+.*", "")       // 去除@标签
//                .trim()                          // 去除首尾空格
//                .replaceAll("\\s+", " ");        // 合并内部空格
//    }
//
//    /**
//     * 获取字段的注释信息
//     */
//    private String getFieldComment(PsiField field) {
//        // 获取JavaDoc注释
//        PsiDocComment docComment = field.getDocComment();
//        if (docComment != null) {
//            return docComment.getText().replaceAll("\\*", "").trim();
//        }
//
//        // 获取行注释
//        PsiElement prevSibling = field.getPrevSibling();
//        while (prevSibling != null) {
//            if (prevSibling instanceof PsiComment) {
//                return prevSibling.getText().replaceAll("//", "").trim();
//            }
//            prevSibling = prevSibling.getPrevSibling();
//        }
//
//        return "";
//    }
//
//    /**
//     * 获取方法的注释信息
//     */
//    private String getMethodComment(PsiMethod method) {
//        PsiDocComment docComment = method.getDocComment();
//        if (docComment != null) {
//            return docComment.getText().replaceAll("\\*", "").trim();
//        }
//        return "";
//    }
//
//    /**
//     * 通过遍历所有方法查找指定名称的方法
//     * @param psiClass 要搜索的类
//     * @param methodName 要查找的方法名
//     * @return 找到的方法，如果不存在则返回null
//     */
//    private PsiMethod findMethodByName(PsiClass psiClass, String methodName) {
//        for (PsiMethod method : psiClass.getAllMethods()) {
//            if (method.getName().equals(methodName)) {
//                return method;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 字符串首字母大写
//     * @param str 输入字符串
//     * @return 首字母大写的字符串
//     */
//    private String capitalize(String str) {
//        if (str == null || str.isEmpty()) {
//            return str;
//        }
//        return str.substring(0, 1).toUpperCase() + str.substring(1);
//    }
//
//    /**
//     * 获取类中所有getter方法
//     * @param psiClass 要分析的类
//     * @return getter方法列表
//     */
//    public List<PsiMethod> getAllGetters(PsiClass psiClass) {
//        List<PsiMethod> getters = new ArrayList<>();
//        for (PsiMethod method : psiClass.getAllMethods()) {
//            String methodName = method.getName();
//            if ((methodName.startsWith("get") && methodName.length() > 3 &&
//                    method.getParameterList().isEmpty()) ||
//                    (methodName.startsWith("is") && methodName.length() > 2 &&
//                            method.getParameterList().isEmpty() &&
//                            "boolean".equals(method.getReturnType().getCanonicalText()))) {
//                getters.add(method);
//            }
//        }
//        return getters;
//    }
//
//    /**
//     * 获取类中所有setter方法
//     * @param psiClass 要分析的类
//     * @return setter方法列表
//     */
//    public List<PsiMethod> getAllSetters(PsiClass psiClass) {
//        List<PsiMethod> setters = new ArrayList<>();
//        for (PsiMethod method : psiClass.getAllMethods()) {
//            String methodName = method.getName();
//            if (methodName.startsWith("set") && methodName.length() > 3 &&
//                    method.getParameterList().getParametersCount() == 1) {
//                setters.add(method);
//            }
//        }
//        return setters;
//    }
//}