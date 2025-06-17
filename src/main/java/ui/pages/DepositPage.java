package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class DepositPage extends BasePage<DepositPage> {
    private SelenideElement accountSelector = $(Selectors.byCssSelector(".form-control.account-selector"));
    private SelenideElement amountInput = $(Selectors.byCssSelector(".form-control[placeholder='Enter amount']"));
    private SelenideElement depositButton = $(Selectors.byText("💵 Deposit"));

    @Override
    public String url() {
        return "/deposit";
    }

    public DepositPage selectAccount(String accountId) {
        accountSelector.selectOptionByValue(accountId);
        return this;
    }

    public DepositPage enterAmount(float amount) {
        amountInput.setValue(String.valueOf(amount));
        return this;
    }

    public DepositPage clickDeposit() {
        depositButton.click();
        return this;
    }
}
