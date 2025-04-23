package ee.carlrobert.codegpt.settings.prompts.form.details

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.TextRange
import com.intellij.ui.JBColor
import java.awt.Dimension
import javax.swing.JPanel

abstract class AbstractEditorPromptPanel(
    private val details: FormPromptDetails,
    highlightedPlaceholders: List<String> = emptyList()
) : Disposable {
    protected val editor: Editor = createEditor()
    private val highlighterMap = mutableMapOf<String, RangeHighlighter>()

    init {
        highlightedPlaceholders.forEach {
            highlightPlaceholder(it)
        }
    }

    abstract fun getPanel(): JPanel

    private fun createEditor(): Editor {
        return service<EditorFactory>()
            .run {
                try {
                    // 预处理字符串，将 "\r\n" 替换为 "\n"
                    val processedInstructions = (details.instructions ?: "").replace("\r\n", "\n")
                    val document = createDocument(processedInstructions?: "");
                    createEditor(document)
                } catch (e: AssertionError) {
                    // 捕获 AssertionError 并处理
                    println("捕获到 AssertionError: ${e.message}")
                    // 可以在这里记录日志或其他处理逻辑
                    // 如果需要，可以返回一个默认的 Editor 或抛出自定义异常
                    // 例如，返回一个空的 Editor
                    createEditor(createDocument(""))
                }
            }
            .apply {
                settings.additionalLinesCount = 0
                settings.isWhitespacesShown = true
                settings.isLineMarkerAreaShown = false
                settings.isIndentGuidesShown = false
                settings.isLineNumbersShown = false
                settings.isFoldingOutlineShown = false
                settings.isUseSoftWraps = true
                settings.isAdditionalPageAtBottom = false
                settings.isVirtualSpace = false
                settings.setSoftMargins(emptyList<Int>())

                component.preferredSize = Dimension(0, lineHeight * 16)

                document.addDocumentListener(object : DocumentListener {
                    override fun documentChanged(event: DocumentEvent) {
                        details.instructions = event.document.text

                        highlighterMap.forEach { (placeholder, highlighter) ->
                            val start = editor.document.text.indexOf(placeholder)
                            if (start == -1) {
                                if (highlighter.isValid) {
                                    highlighter.dispose()
                                }
                            } else if (!highlighter.isValid) {
                                highlighterMap[placeholder] =
                                    createHighlighter(TextRange.from(start, placeholder.length))
                            }
                        }
                    }
                })
            }
    }

    private fun createHighlighter(range: TextRange): RangeHighlighter {
        val attributes = TextAttributes().apply {
            foregroundColor = JBColor(0x00627A, 0xCC7832)
        }

        return editor.markupModel.addRangeHighlighter(
            range.startOffset,
            range.endOffset,
            HighlighterLayer.SELECTION,
            attributes,
            HighlighterTargetArea.EXACT_RANGE
        )
    }

    private fun highlightPlaceholder(placeholder: String) {
        val start = editor.document.text.indexOf(placeholder)
        if (start >= 0) {
            highlighterMap[placeholder] =
                createHighlighter(TextRange.from(start, placeholder.length))
        }
    }

    protected fun updateEditorText(text: String?) {
        runWriteAction {
            editor.document.setText(text ?: "")
        }
    }

    override fun dispose() {
        EditorFactory.getInstance().releaseEditor(editor)
    }
}
