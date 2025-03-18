package ee.carlrobert.codegpt.toolwindow.chat.ui;

import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;

import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.openapi.roots.ui.componentsList.layout.VerticalStackLayout;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.cjhx.utils.RedisUtil;
import ee.carlrobert.codegpt.credentials.CredentialsStore;
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceConfigurable;
import ee.carlrobert.codegpt.toolwindow.ui.ResponseMessagePanel;
import ee.carlrobert.codegpt.ui.UIUtil;
import ee.carlrobert.codegpt.util.ApplicationUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class ChatToolWindowScrollablePanel extends ScrollablePanel {

  private final Map<UUID, JPanel> visibleMessagePanels = new HashMap<>();

  public ChatToolWindowScrollablePanel() {
    super(new VerticalStackLayout());
  }

  public void displayLandingView(JComponent landingView) {
    String keyAndUrl = RedisUtil.getKeyAndUrl();
    String apiUrl = "未获取到，请联系管理员！";
    String apiKey = "未获取到，请联系管理员！";
    if(!StringUtils.isEmpty(keyAndUrl) && !StringUtils.isEmpty(keyAndUrl.replace(",",""))){
      apiUrl = keyAndUrl.split(",")[0];
      apiKey = keyAndUrl.split(",")[1];
    }
    clearAll();
    add(landingView);
    if (GeneralSettings.isSelected(ServiceType.CODEGPT)
        && !CredentialsStore.INSTANCE.isCredentialSet(CredentialKey.CodeGptApiKey.INSTANCE)) {

      var panel = new ResponseMessagePanel();
      panel.addContent(UIUtil.createTextPane(String.format("""
              <html>
              <p style="margin-top: 4px; margin-bottom: 4px;">
              看起来你还没有配置你的API密钥，请访问ProxyAI设置
              </p>
              <p style="margin-top: 4px; margin-bottom: 4px;">
              　　操作路径如下：File->Settings->Tools->ProxyAI->Providers->Custom OpenAI
              </p>
              <p style="margin-top: 4px; margin-bottom: 4px;">
              　　配置 API Key ： <span style="color: #589DF6;">%s</span>
              </p>
              <p style="margin-top: 4px; margin-bottom: 4px;">
              　　配置 URL　　： <span style="color: #589DF6;">%s</span> （Chat Completions 以及 Code Completions）
              </p>
              </html>""",apiKey,apiUrl),
              false,
              event -> {
                if (ACTIVATED.equals(event.getEventType())
                        && "#OPEN_SETTINGS".equals(event.getDescription())) {
                  ShowSettingsUtil.getInstance().showSettingsDialog(
                          ApplicationUtil.findCurrentProject(),
                          CodeGPTServiceConfigurable.class);
                } else {
                  UIUtil.handleHyperlinkClicked(event);
                }
              }));

      /*panel.addContent(UIUtil.createTextPane("""
              <html>
              <p style="margin-top: 4px; margin-bottom: 4px;">
              看起来你还没有配置你的API密钥，请访问ProxyAI设置
              </p>
              <p style="margin-top: 4px; margin-bottom: 4px;">
              　　操作路径如下：File->Settings->Tools->ProxyAI->Providers->Custom OpenAI
              </p>
              <p style="margin-top: 4px; margin-bottom: 4px;">
              　　配置 API Key ： <span style="color: #589DF6;">app-vO3P1xzlwofvGBEMz0REIJhz</span>
              </p>
              <p style="margin-top: 4px; margin-bottom: 4px;">
              　　配置 URL     ： <span style="color: #589DF6;">http://10.190.220.33:3000/v1/chat/completions</span> （Chat Completions 以及 Code Completions）
              </p>
              </html>""",
              false,
              event -> {
                if (ACTIVATED.equals(event.getEventType())
                        && "#OPEN_SETTINGS".equals(event.getDescription())) {
                  ShowSettingsUtil.getInstance().showSettingsDialog(
                          ApplicationUtil.findCurrentProject(),
                          CodeGPTServiceConfigurable.class);
                } else {
                  UIUtil.handleHyperlinkClicked(event);
                }
              }));*/
      /*panel.addContent(UIUtil.createTextPane("""
              <html>
              <p style="margin-top: 4px; margin-bottom: 4px;">
                It looks like you haven't configured your API key yet. Visit <a href="#OPEN_SETTINGS">ProxyAI settings</a> to do so.
              </p>
              <p style="margin-top: 4px; margin-bottom: 4px;">
                Don't have an account? <a href="https://tryproxy.io/signin">Sign up</a> to get the most out of ProxyAI.
              </p>
              </html>""",
          false,
          event -> {
            if (ACTIVATED.equals(event.getEventType())
                && "#OPEN_SETTINGS".equals(event.getDescription())) {
              ShowSettingsUtil.getInstance().showSettingsDialog(
                  ApplicationUtil.findCurrentProject(),
                  CodeGPTServiceConfigurable.class);
            } else {
              UIUtil.handleHyperlinkClicked(event);
            }
          }));*/
      panel.setBorder(JBUI.Borders.customLine(JBColor.border(), 1, 0, 0, 0));
      add(panel);
    }
  }

  public ResponseMessagePanel getResponseMessagePanel(UUID messageId) {
    return (ResponseMessagePanel) Arrays.stream(visibleMessagePanels.get(messageId).getComponents())
        .filter(ResponseMessagePanel.class::isInstance)
        .findFirst().orElseThrow();
  }

  public JPanel addMessage(UUID messageId) {
    var messageWrapper = new JPanel();
    messageWrapper.setLayout(new BoxLayout(messageWrapper, BoxLayout.PAGE_AXIS));
    add(messageWrapper);
    visibleMessagePanels.put(messageId, messageWrapper);
    return messageWrapper;
  }

  public void removeMessage(UUID messageId) {
    remove(visibleMessagePanels.get(messageId));
    update();
    visibleMessagePanels.remove(messageId);
  }

  public void clearAll() {
    visibleMessagePanels.clear();
    removeAll();
    update();
  }

  public void update() {
    repaint();
    revalidate();
  }
}
