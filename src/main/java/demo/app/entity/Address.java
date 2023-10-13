package demo.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "address")
public class Address {
    @Id
    @Column(name = "address_id")
    private String addressId;

    private String street;
    private String city;
    private String province;
    private String country;

    @Column(name = "postal_code")
    private String postalCode;

}
