package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class UserDashboard extends BasePage<UserDashboard> {
    private SelenideElement welcomeText = $(Selectors.byCssSelector(".welcome-text"));
    private SelenideElement createNewAccount = $(Selectors.byText("➕ Create New Account"));
    private SelenideElement depositMoney = $(Selectors.byText("💰 Deposit Money"));
    private SelenideElement makeATransfer = $(Selectors.byText("🔄 Make a Transfer"));

    @Override
    public String url() {
        return "/dashboard";
    }

    public UserDashboard createNewAccount() {
        createNewAccount.click();
        return this;
    }

    public UserDashboard depositMoney() {
        depositMoney.click();
        return this;
    }

    public UserDashboard transferMoney() {
        makeATransfer.click();
        return this;
    }

    public UserDashboard clickUsernameTitle(String username) {
        $(Selectors.byText(username)).click();
        return this;
    }
}
