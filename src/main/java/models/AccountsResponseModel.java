package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountsResponseModel extends BaseModel {
    private Long id;
    private String accountNumber;
    private Float balance;
    private List<TransactionsResponseModel> transactions;
}
