package ee.carlrobert.codegpt.toolwindow.ui

import com.intellij.ui.components.ActionLink
import com.intellij.util.ui.JBUI
import ee.carlrobert.codegpt.Icons
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.prompts.ChatActionsState
import ee.carlrobert.codegpt.ui.UIUtil.createTextPane
import java.awt.BorderLayout
import java.awt.Point
import java.awt.event.ActionListener
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel

class ChatToolWindowLandingPanel(onAction: (LandingPanelAction, Point) -> Unit) : ResponseMessagePanel() {

    init {
        addContent(createContent(onAction))
    }

    private fun createContent(onAction: (LandingPanelAction, Point) -> Unit): JPanel {
        return JPanel(BorderLayout()).apply {
            add(createTextPane(getWelcomeMessage(), false), BorderLayout.NORTH)
            add(createActionsListPanel(onAction), BorderLayout.CENTER)
            add(createTextPane(getCautionMessage(), false), BorderLayout.SOUTH)
        }
    }

    private fun createActionsListPanel(onAction: (LandingPanelAction, Point) -> Unit): JPanel {
        val listPanel = JPanel()
        listPanel.layout = BoxLayout(listPanel, BoxLayout.PAGE_AXIS)
        listPanel.border = JBUI.Borders.emptyLeft(4)
        listPanel.add(Box.createVerticalStrut(4))
        listPanel.add(createEditorActionLink(LandingPanelAction.EXPLAIN, onAction))
        listPanel.add(Box.createVerticalStrut(4))
        listPanel.add(createEditorActionLink(LandingPanelAction.WRITE_TESTS, onAction))
        listPanel.add(Box.createVerticalStrut(4))
        listPanel.add(createEditorActionLink(LandingPanelAction.FIND_BUGS, onAction))
        listPanel.add(Box.createVerticalStrut(4))
        return listPanel
    }

    private fun createEditorActionLink(
        action: LandingPanelAction,
        onAction: (LandingPanelAction, Point) -> Unit
    ): ActionLink {
        return ActionLink(action.userMessage, ActionListener { event ->
            onAction(action, (event.source as ActionLink).locationOnScreen)
        }).apply {
            icon = Icons.Sparkle
        }
    }

    private fun getWelcomeMessage(): String {
        /*return """
            <html>
            <p style="margin-top: 4px; margin-bottom: 4px;">
            Hi <strong>${GeneralSettings.getCurrentState().displayName}</strong>, I'm ProxyAI! You can ask me anything, but most people request help with their code. Here are a few examples of what you can ask me:
            </p>
            </html>
        """.trimIndent()*/

        return """
            <html>
            <p style="margin-top: 4px; margin-bottom: 4px;">
            嗨 <strong>${GeneralSettings.getCurrentState().displayName}</strong>, 我是 CatJCodeAI！
            </p>
            <p style="margin-top: 4px; margin-bottom: 4px;">
            你可以问我任何问题，但大多数人都要求我帮助他们编写代码。下面是一些你可以问我的问题：
            </p>
            </html>
        """.trimIndent()
    }

    private fun getCautionMessage(): String {
        /*return """
            <html>
            <p style="margin-top: 4px; margin-bottom: 4px;">
            I can sometimes make mistakes, so please double-check anything critical.
            </p>
            </html>
        """.trimIndent()*/
        return """
            <html>
            <p style="margin-top: 4px; margin-bottom: 4px;">
            扩展内容：
            </p>
            <p style="margin-top: 4px; margin-bottom: 4px;color: #589DF6;">
            　　1. 读取创金知识库，例如：API平台接口信息、生成API平台接口代码等
            </p>
            <p style="margin-top: 4px; margin-bottom: 4px;color: #589DF6;">
            　　2. 根据表名生成CRUD，例如：根据数据库及表名生成 基础SQL、java代码（增删改查、分页）
            </p>
            <p style="margin-top: 4px; margin-bottom: 4px;color: #589DF6;">
            　　3. 快捷键：<br/>
            　　　　　　　　根据注释生成代码　　（Ctrl+A 或 右键）<br/>
            　　　　　　　　根据注释生成方法代码（Ctrl+S 或 右键）<br/>
            　　　　　　　　新聊天　　　　　　　（Ctrl+Alt+Shift+N　 或　右键-CatJCodeAI）<br/>
            　　　　　　　　在提示词中包含选择　（Ctrl+Shift+I　　　　或　右键-CatJCodeAI）<br/>
            　　　　　　　　询问问题　　　　　　（Ctrl+Alt+Shift+Q　 或　右键-CatJCodeAI）<br/>
            　　　　　　　　编辑目标代码　　　　（Ctrl+Shift+K　 　　  或　右键-CatJCodeAI）<br/>
            　　　　　　　　查找目标BUG　　　　（右键-CatJCodeAI）<br/>
            　　　　　　　　编写目标测试　　　　（右键-CatJCodeAI）<br/>
            　　　　　　　　解释目标代码　　　　（右键-CatJCodeAI）<br/>
            　　　　　　　　重构目标代码　　　　（右键-CatJCodeAI）<br/>
            　　　　　　　　优化目标代码　　　　（右键-CatJCodeAI）
            </p>
            <p style="margin-top: 1px; margin-bottom: 1px;">
            </p>
            <p style="margin-top: 4px; margin-bottom: 4px;">
            获取最新秘钥：<a href="http://10.190.220.33:3000/api/getCodeGptKeyAndUrl" style="text-decoration: underline">http://10.190.220.33:3000/api/getCodeGptKeyAndUrl（单击）</a>
            </p>
            <p style="margin-top: 4px; margin-bottom: 4px;">
            获取最新版本：<a href="file:\\10.201.210.200\temp\CatJCodeAI_ReleaseVersions" style="text-decoration: underline">\\10.201.210.200\temp\CatJCodeAI_ReleaseVersions（单击）</a>
            </p>
            <p style="margin-top: 4px; margin-bottom: 4px;">
            我有时会犯错误，所以请仔细检查任何重要的事情。
            </p>
            </html>
        """.trimIndent()
    }
}

enum class LandingPanelAction(
    val label: String,
    val userMessage: String,
    val prompt: String
) {

    FIND_BUGS(
        "Find Bugs",
        "查找代码中的BUG",
        ChatActionsState.DEFAULT_FIND_BUGS_PROMPT
    ),
    WRITE_TESTS(
        "Write Tests",
        "为代码编写单元测试",
        ChatActionsState.DEFAULT_WRITE_TESTS_PROMPT
    ),
    EXPLAIN(
        "Explain",
        "解释选定的代码",
        ChatActionsState.DEFAULT_EXPLAIN_PROMPT
    )
    /*FIND_BUGS(
        "Find Bugs",
        "Find bugs in this code",
        ChatActionsState.DEFAULT_FIND_BUGS_PROMPT
    ),
    WRITE_TESTS(
        "Write Tests",
        "Write unit tests for this code",
        ChatActionsState.DEFAULT_WRITE_TESTS_PROMPT
    ),
    EXPLAIN(
        "Explain",
        "Explain the selected code",
        ChatActionsState.DEFAULT_EXPLAIN_PROMPT
    )*/
}

