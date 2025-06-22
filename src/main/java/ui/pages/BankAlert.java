package ui.pages;

import lombok.Getter;

@Getter
public enum BankAlert {
    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS("Username must be between 3 and 15 characters"),
    NEW_ACCOUNT_CREATED("✅ New Account Created! Account Number: "),
    DEPOSIT_SUCCESSFUL("✅ Successfully deposited "),
    TRANSFER_SUCCESSFUL("Successfully transferred"),
    NAME_UPDATED_SUCCESSFULLY("✅ Name updated successfully!"),
    DEPOSIT_AMOUNT_EXCEEDS_LIMIT("❌ Please deposit less or equal to 5000$."),
    TRANSFER_AMOUNT_EXCEEDS_LIMIT("❌ Error: Transfer amount cannot exceed 10000"),
    NAME_INVALID("❌ Please enter a valid name."),
    NAME_SAME_AS_CURRENT("⚠️ New name is the same as the current one."),
    FILL_ALL_FIELDS_AND_CONFIRM("❌ Please fill all fields and confirm."),
    NO_USER_FOUND_WITH_ACCOUNT_NUMBER("❌ No user found with this account number."),
    INVALID_TRANSFER("❌ Error: Invalid transfer: insufficient funds or invalid accounts"),
    INVALID_DEPOSIT_AMOUNT("❌ Please enter a valid amount.");

    private final String message;

    BankAlert(String message) {
        this.message = message;
    }
}
