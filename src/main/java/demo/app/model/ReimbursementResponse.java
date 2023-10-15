package demo.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReimbursementResponse {
    private Long reimbursementId;
    private Long employeeId;
    private BigDecimal amount;
    private String approved;
    private String currency;
    private String description;
    private Boolean status;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
}
