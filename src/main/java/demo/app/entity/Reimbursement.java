package demo.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reimbursement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reimbursement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reimbursement_id")
    private Long reimbursementId;

    @Column(name = "approved_id")
    private String approvedId;

    @Column(name = "approved_name")
    private String approvedName;

    private BigDecimal amount;

    private String activity;

    @Column(name = "type")
    private String typeReimbursement;

    private String description;

    private Boolean status;

//    @Lob
//    private byte[] file;
//
//    @Column(name = "file_name")
//    private String fileName;
//
//    @Column(name = "file_type")
//    private String fileType;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
