package ee.carlrobert.codegpt.toolwindow.ui

import com.intellij.internal.PrintModulesAndEntitySources.Companion.log
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBFont
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.Icons
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import javax.swing.SwingConstants

open class ResponseMessagePanel : BaseMessagePanel() {

    override fun createDisplayNameLabel(): JBLabel {
        return JBLabel(
            "CatJCodeAI",
            Icons.Default,
            SwingConstants.LEADING
        )
        .setAllowAutoWrapping(true)
        .withFont(JBFont.label().asBold())
        .apply {
            iconTextGap = 6
        }
    }
}