package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionsResponse extends BaseModel {
    private String id;
    private Float amount;
    private String type;
    private String timestamp;
    private Integer relatedAccountId;
}
