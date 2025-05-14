package ee.carlrobert.codegpt.cjhx;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

public class GenerateSwaggerParamsAction extends AnAction {

    private static final Logger log = LoggerFactory.getLogger(GenerateSwaggerParamsAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) return;

        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) return;

        PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);

        List<String> excludedFields = List.of(
                "sysToken", "serialVersionUID", "rows", "currentIndex",
                "orderbys", "orderBy", "ascOrDesc", "orderbyString",
                "orclBegin", "orclEnd"
        );

        if (containingMethod != null) {
            StringBuilder swaggerAnnotation = new StringBuilder();
            swaggerAnnotation.append("@ApiInfo(params = {\n");

            PsiParameter[] parameters = containingMethod.getParameterList().getParameters();
            List<String> paramLines = new ArrayList<>();

            for (PsiParameter parameter : parameters) {
                PsiType type = parameter.getType();
                String typeName = type.getCanonicalText();

                // �ų� servlet ��ܲ���
                if (typeName.equals("javax.servlet.http.HttpServletRequest") ||
                        typeName.equals("javax.servlet.http.HttpServletResponse") ||
                        typeName.equals("javax.servlet.ServletRequest") ||
                        typeName.equals("javax.servlet.ServletResponse")) {
                    continue;
                }

                if (isBasicType(typeName)) {
                    paramLines.add(String.format(
                            "    @ApiParam(name=\"%s\", code=\"%s\", value=\"\", clazz=%s.class, required=true)",
                            parameter.getName(),
                            parameter.getName(),
                            typeName
                    ));
                } else if (typeName.startsWith("java.util.Map") || typeName.startsWith("java.util.List") || typeName.startsWith("java.util.Set")) {
                    // ���Ͳ���ͳһֻ����һ��
                    paramLines.add(String.format(
                            "    @ApiParam(name=\"%s\", code=\"%s\", value=\"\", clazz=%s.class, required=true)",
                            parameter.getName(),
                            parameter.getName(),
                            PsiUtil.resolveClassInType(type) != null ? PsiUtil.resolveClassInType(type).getQualifiedName() : "java.lang.Object"
                    ));
                } else {
                    PsiClass psiClass = PsiUtil.resolveClassInType(type);
                    if (psiClass != null) {


                        for (PsiField field : psiClass.getAllFields()) {
                            String fieldName = field.getName();

                            if (excludedFields.contains(fieldName)) {
                                continue;
                            }

                            String fieldType = field.getType().getPresentableText();
                            String comment = getCleanFieldComment(field);

                            paramLines.add(String.format(
                                    "    @ApiParam(name=\"%s\", code=\"%s\", value=\"\", clazz=%s.class)",
                                    comment.isEmpty() ? fieldName : comment.replaceAll("\\n", " "),
                                    fieldName,
                                    fieldType
                            ));
                        }
                    }
                }
            }

            swaggerAnnotation.append(String.join(",\n", paramLines));
            swaggerAnnotation.append("\n}, response = {\n");
            swaggerAnnotation.append("    @ApiRes(name=\"��Ӧ��[0�ɹ�/-1ʧ��]\", code=\"code\", clazz=String.class, value=\"0\"),\n");
            swaggerAnnotation.append("    @ApiRes(name=\"��Ӧ��Ϣ\", code=\"message\", clazz=String.class, value=\"success\"),\n");
            swaggerAnnotation.append("    @ApiRes(name=\"��������\", code=\"body\", clazz=Object.class, value=\"\")\n");
            swaggerAnnotation.append("})\n");

            // ���Ƶ�������
            CopyPasteManager.getInstance().setContents(new StringSelection(swaggerAnnotation.toString()));

            // ����ע�⣨������ WriteCommandAction ��ִ�У�
            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
                    PsiAnnotation annotation = elementFactory.createAnnotationFromText(swaggerAnnotation.toString(), containingMethod);
                    PsiModifierList modifierList = containingMethod.getModifierList();
                    modifierList.addBefore(annotation, modifierList.getFirstChild());

                    Messages.showInfoMessage(project, "Swaggerע��������\n�����뵽�����Ϸ� ���Ѹ���", "�ɹ�");
                } catch (Exception e) {
                    log.error("����Swaggerע��ʧ��", e);
                    Messages.showErrorDialog(project, "����Swaggerע��ʧ��: \n" + e.getMessage(), "����");
                }
            });
        }
    }

    private boolean isBasicType(String typeName) {
        return typeName.equals("byte") || typeName.equals("short") || typeName.equals("int") ||
                typeName.equals("long") || typeName.equals("float") || typeName.equals("double") ||
                typeName.equals("char") || typeName.equals("boolean") ||
                typeName.equals("java.lang.Byte") || typeName.equals("java.lang.Short") ||
                typeName.equals("java.lang.Integer") || typeName.equals("java.lang.Long") ||
                typeName.equals("java.lang.Float") || typeName.equals("java.lang.Double") ||
                typeName.equals("java.lang.Character") || typeName.equals("java.lang.Boolean") ||
                typeName.equals("java.lang.String") || typeName.equals("java.util.Date");
    }

    private String getCleanFieldComment(PsiField field) {
        PsiDocComment docComment = field.getDocComment();
        if (docComment != null) {
            return cleanJavaDocComment(docComment.getText());
        }

        String fieldText = field.getText();
        StringBuilder commentBuilder = new StringBuilder();

        if (fieldText.contains("/*") && fieldText.contains("*/")) {
            String comment = fieldText.substring(fieldText.indexOf("/*"), fieldText.indexOf("*/") + 2);
            commentBuilder.append(comment.replaceAll("/\\*|\\*/", "").trim());
        } else if (fieldText.contains("//")) {
            String comment = fieldText.substring(fieldText.indexOf("//") + 2);
            int endIndex = comment.indexOf("private");
            if (endIndex > 0) {
                comment = comment.substring(0, endIndex);
            }
            commentBuilder.append(comment.trim());
        } else if (fieldText.contains("/**") && fieldText.contains("*/")) {
            String comment = fieldText.substring(fieldText.indexOf("/**"), fieldText.indexOf("*/") + 2);
            commentBuilder.append(cleanJavaDocComment(comment));
        }

        PsiModifierList modifierList = field.getModifierList();
        if (modifierList != null) {
            for (PsiElement child : modifierList.getChildren()) {
                if (child instanceof PsiComment) {
                    String commentText = child.getText();
                    if (commentText.startsWith("//")) {
                        if (commentBuilder.length() > 0) {
                            commentBuilder.append(" ");
                        }
                        commentBuilder.append(commentText.substring(2).trim());
                    }
                }
            }
        }

        return commentBuilder.toString().trim();
    }

    private String cleanJavaDocComment(String text) {
        return text.replaceAll("/\\*\\*|\\*/", "")
                .replaceAll("(?m)^\\s*\\*\\s*", "")
                .replaceAll("@\\w+.*", "")
                .replaceAll("\\n\\s*", " ")
                .trim();
    }
}