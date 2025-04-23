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
//                    combinedInfo.append("����: ").append(parameter.getName())
//                            .append(" (����: ").append(psiClass.getQualifiedName()).append(")\n\n");
//
//                    // ���������ֶ�
//                    for (PsiField field : psiClass.getAllFields()) {
//                        combinedInfo.append(field.getName())
//                                .append(" (").append(field.getType().getPresentableText()).append(")");
//
//                        // ��ȡ����ʽ���ֶ�ע��
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
//                log.info("�ֶη������:\n{}", combinedInfo.toString());
//                Messages.showInfoMessage(project, combinedInfo.toString(), "�ֶ���Ϣ");
//            }
//        }
//    }
//
//    /**
//     * ��ȡ�������ֶ�ע�ͣ��޸���ע�ͻ�ȡ���⣩
//     */
//    private String getCleanFieldComment(PsiField field) {
//        // 1. �ȳ��Ի�ȡJavaDocע�ͣ�/** */ ���֣�
//        PsiDocComment docComment = field.getDocComment();
//        if (docComment != null) {
//            return cleanJavaDocComment(docComment.getText());
//        }
//
//        // 2. ��ȡ�ֶ�ǰ������ע�ͣ�������ע�ͣ�
//        PsiElement prevSibling = field.getPrevSibling();
//        while (prevSibling != null) {
//            // �����հ׺�ע��
//            if (prevSibling instanceof PsiWhiteSpace || prevSibling instanceof PsiAnnotation) {
//                prevSibling = prevSibling.getPrevSibling();
//                continue;
//            }
//
//            // �ҵ������ע��
//            if (prevSibling instanceof PsiComment) {
//                String commentText = prevSibling.getText();
//                // ������ע��
//                if (commentText.startsWith("//")) {
//                    return commentText.substring(2).trim();
//                }
//                // �����ע�� /* */
//                if (commentText.startsWith("/*")) {
//                    return commentText.replaceAll("/\\*|\\*/", "").trim();
//                }
//            }
//            break; // ������ע��Ԫ�ؾ�ֹͣ����
//        }
//
//        return ""; // û���ҵ�ע��
//    }
//
//    /**
//     * ����JavaDocע��
//     */
//    private String cleanJavaDocComment(String text) {
//        return text.replaceAll("/\\*\\*|\\*/", "") // ȥ��ͷβ���
//                .replaceAll("\\*", "")           // ȥ��ÿ���Ǻ�
//                .replaceAll("@\\w+.*", "")       // ȥ��@��ǩ
//                .trim()                          // ȥ����β�ո�
//                .replaceAll("\\s+", " ");        // �ϲ��ڲ��ո�
//    }
//
//    /**
//     * ��ȡ�ֶε�ע����Ϣ
//     */
//    private String getFieldComment(PsiField field) {
//        // ��ȡJavaDocע��
//        PsiDocComment docComment = field.getDocComment();
//        if (docComment != null) {
//            return docComment.getText().replaceAll("\\*", "").trim();
//        }
//
//        // ��ȡ��ע��
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
//     * ��ȡ������ע����Ϣ
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
//     * ͨ���������з�������ָ�����Ƶķ���
//     * @param psiClass Ҫ��������
//     * @param methodName Ҫ���ҵķ�����
//     * @return �ҵ��ķ���������������򷵻�null
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
//     * �ַ�������ĸ��д
//     * @param str �����ַ���
//     * @return ����ĸ��д���ַ���
//     */
//    private String capitalize(String str) {
//        if (str == null || str.isEmpty()) {
//            return str;
//        }
//        return str.substring(0, 1).toUpperCase() + str.substring(1);
//    }
//
//    /**
//     * ��ȡ��������getter����
//     * @param psiClass Ҫ��������
//     * @return getter�����б�
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
//     * ��ȡ��������setter����
//     * @param psiClass Ҫ��������
//     * @return setter�����б�
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