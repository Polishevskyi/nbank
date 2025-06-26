package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class ProfilePage extends BasePage<ProfilePage> {

    private SelenideElement newNameInput = $(Selectors.byCssSelector("input[placeholder=\"Enter new name\"]"));
    private SelenideElement saveChangesBtn = $(Selectors.byCssSelector("button.btn.btn-primary.mt-3"));
    private SelenideElement profileName = $(Selectors.byClassName("user-name"));

    @Override
    public String url() {
        return "/profile";
    }

    public ProfilePage enterNewName(String newName) {
        newNameInput.setValue(newName);
        return this;
    }

    public ProfilePage clickSaveChanges() {
        saveChangesBtn.click();
        return this;
    }

    public String getProfileNameText() {
        return profileName.getText();
    }
}