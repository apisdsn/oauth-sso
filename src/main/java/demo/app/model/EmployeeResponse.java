package demo.app.model;

import demo.app.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeResponse{
    private Long employeeId;

    private String clientId;

    private String fullName;

    private String phoneNumber;

    private String email;

    private String company;

    private String position;

    private String gender;

    private AddressResponse address;
}
