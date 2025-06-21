package common.extensions;

import com.codeborne.selenide.Configuration;
import common.annotations.Browsers;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Arrays;

public class BrowserMatchExtension implements ExecutionCondition {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
        Browsers annotation = extensionContext.getElement()
                .map(el -> el.getAnnotation(Browsers.class))
                .orElse(null);

        if (annotation == null) {
            return ConditionEvaluationResult.enabled("There are no restrictions on the browser");
        }

        String currentBrowser = Configuration.browser;
        boolean matches = Arrays.stream(annotation.value())
                .anyMatch(browser -> browser.equals(currentBrowser));

        if (matches) {
            return ConditionEvaluationResult.enabled("The current browser satisfies the condition: "+ currentBrowser);
        } {
            return ConditionEvaluationResult.disabled("Test skipped because the current browser " + currentBrowser +
                    " is not on the list of acceptable browsers for the test: " + Arrays.toString(annotation.value()));
        }
    }
}
