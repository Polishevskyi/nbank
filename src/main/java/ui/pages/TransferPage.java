package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class TransferPage extends BasePage<TransferPage> {
    private SelenideElement sourceAccountSelector = $(Selectors.byCssSelector("select.form-control.account-selector"));
    private SelenideElement recipientNameInput = $(Selectors.byCssSelector("input[placeholder=\"Enter recipient name\"]"));
    private SelenideElement recipientAccountNumberInput = $(Selectors.byCssSelector("input[placeholder=\"Enter recipient account number\"]"));
    private SelenideElement amountInput = $(Selectors.byCssSelector("input[placeholder=\"Enter amount\"]"));
    private SelenideElement confirmCheck = $(Selectors.byCssSelector("#confirmCheck"));
    private SelenideElement sendTransferButton = $(Selectors.byText("🚀 Send Transfer"));

    @Override
    public String url() {
        return "/transfer";
    }

    public TransferPage selectSourceAccount(String accountId) {
        sourceAccountSelector.selectOptionByValue(accountId);
        return this;
    }

    public TransferPage enterRecipientName(String name) {
        recipientNameInput.setValue(name);
        return this;
    }

    public TransferPage enterRecipientAccountNumber(String accountNumber) {
        recipientAccountNumberInput.setValue(accountNumber);
        return this;
    }

    public TransferPage enterAmount(float amount) {
        amountInput.setValue(String.valueOf(amount));
        return this;
    }

    public TransferPage clickConfirmCheck() {
        confirmCheck.click();
        return this;
    }

    public TransferPage clickSendTransfer() {
        sendTransferButton.click();
        return this;
    }
}