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
//                // �ж��Ƿ��ǻ������ͻ��ð�װ����
//                if (isBasicType(typeName)) {
//                    // ֱ�Ӵ���������Ͳ���
//                    paramLines.add(String.format(
//                            "    @ApiParam(name=\"%s\", code=\"%s\", value=\"\", dataType=\"%s\", required=true)",
//                            parameter.getName(),
//                            parameter.getName(),
//                            typeName
//                    ));
//                } else {
//                    // ����������ͣ���ȡ���ֶ�
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
//            swaggerAnnotation.append("    @ApiRes(name=\"��Ӧ��[0�ɹ�/-1ʧ��]\", code=\"code\", clazz=String.class, value=\"0\"),\n");
//            swaggerAnnotation.append("    @ApiRes(name=\"��Ӧ��Ϣ\", code=\"message\", clazz=String.class, value=\"success\"),\n");
//            swaggerAnnotation.append("    @ApiRes(name=\"��������\", code=\"body\", clazz=Object.class, value=\"\")\n");
//            swaggerAnnotation.append("})");
//
//            // �����ɵ�Swaggerע�⸴�Ƶ�������
//            CopyPasteManager.getInstance().setContents(new StringSelection(swaggerAnnotation.toString()));
//
//            // ��ʾ�ɹ���Ϣ
//            Messages.showInfoMessage(project, "Swaggerע�������ɲ����Ƶ�������", "�ɹ�");
//        }
//    }
//
//    // �ж��Ƿ��ǻ������ͻ��ð�װ����
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