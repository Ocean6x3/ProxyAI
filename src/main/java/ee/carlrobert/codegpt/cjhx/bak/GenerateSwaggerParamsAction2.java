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
//     * ��ȡ�ֶ�ע�ͣ����տɿ��棩
//     */
//    /**
//     * ��ȡ�ֶ�ע�ͣ���ǿ�棩
//     */
//    private String getCleanFieldComment(PsiField field) {
////        printAllPrevElements(field);
////        log.info("++++++++++++++++++++++++++++++");
//        // 1. ���ȼ���ֶ��Դ���JavaDocע��
//        PsiDocComment docComment = field.getDocComment();
//        if (docComment != null) {
//            return cleanJavaDocComment(docComment.getText());
//        }
//
//        // 2. ֱ�Ӵ��ֶ�Ԫ������ȡע��
//        String fieldText = field.getText();
//        StringBuilder commentBuilder = new StringBuilder();
//
//        // �������ע�� /* ... */
//        if (fieldText.contains("/*") && fieldText.contains("*/")) {
//            String comment = fieldText.substring(fieldText.indexOf("/*"), fieldText.indexOf("*/") + 2);
//            commentBuilder.append(comment.replaceAll("/\\*|\\*/", "").trim());
//        }
//        // ������ע�� //
//        else if (fieldText.contains("//")) {
//            String comment = fieldText.substring(fieldText.indexOf("//") + 2);
//            // ֻȡ����β���ֶ�������ʼ��
//            int endIndex = comment.indexOf("private");
//            if (endIndex > 0) {
//                comment = comment.substring(0, endIndex);
//            }
//            commentBuilder.append(comment.trim());
//        }
//        // ����JavaDoc /** ... */
//        else if (fieldText.contains("/**") && fieldText.contains("*/")) {
//            String comment = fieldText.substring(fieldText.indexOf("/**"), fieldText.indexOf("*/") + 2);
//            commentBuilder.append(cleanJavaDocComment(comment));
//        }
//
//        // 3. ���ע����ע��
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
//     * ��ȡǰһ���ǿհ�Ԫ�أ��������пհף�
//     */
//    private PsiElement getPreviousNonWhitespaceElement(PsiElement element) {
//        while (element != null && element instanceof PsiWhiteSpace) {
//            element = element.getPrevSibling();
//        }
//        return element;
//    }
//
//    /**
//     * ����JavaDocע�ͣ��Ż��棩
//     */
//    private String cleanJavaDocComment(String text) {
//        return text.replaceAll("/\\*\\*|\\*/", "")  // ȥ��ͷβ���
//                .replaceAll("(?m)^\\s*\\*\\s*", "") // ȥ��ÿ���Ǻ�
//                .replaceAll("@\\w+.*", "")       // ȥ����ǩ
//                .replaceAll("\\n\\s*", " ")      // ������
//                .trim();
//    }
//
//
//    private void printAllPrevElements(PsiField field) {
//        System.out.println("=== ����ǰ��Ԫ�ط��� ===");
//        System.out.println("�ֶ�: " + field.getName());
//
//        PsiElement prev = field.getPrevSibling();
//        while (prev != null) {
//            System.out.println("Ԫ������: " + prev.getClass().getSimpleName());
//            System.out.println("Ԫ������: " + prev.getText());
//            System.out.println("-----");
//            prev = prev.getPrevSibling();
//        }
//
//        PsiModifierList modifiers = field.getModifierList();
//        if (modifiers != null) {
//            System.out.println("=== ���η��б� ===");
//            for (PsiElement child : modifiers.getChildren()) {
//                System.out.println("���η�Ԫ��: " + child.getClass().getSimpleName());
//                System.out.println("����: " + child.getText());
//            }
//        }
//    }
//}