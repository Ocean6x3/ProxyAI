package ee.carlrobert.codegpt.settings;


import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@State(
        name = "CustomOpenAISettings",
        storages = @Storage("customOpenAISettings.xml")
)
public class CustomOpenAISettings implements PersistentStateComponent<CustomOpenAISettings.State> {
    public static class State {
        public String apiKey;
        public String url;
        public Map<String, String> headers;
    }

    private State myState = new State();

    @NotNull
    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

    public String getApiKey() {
        return myState.apiKey;
    }

    public String getUrl() {
        return myState.url;
    }

    public Map<String, String> getHeaders() {
        return myState.headers;
    }
}
