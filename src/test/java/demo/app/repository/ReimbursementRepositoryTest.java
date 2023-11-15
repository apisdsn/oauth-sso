package demo.app.repository;

import demo.app.entity.Employee;
import demo.app.entity.Reimbursement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
public class ReimbursementRepositoryTest {

    @Autowired
    private ReimbursementRepository reimbursementRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;
    private Reimbursement reimbursement;

    @BeforeEach
    public void setUp() {
        employeeRepository.deleteAll();
        reimbursementRepository.deleteAll();

        employee = new Employee();
        employee.setClientId("123");
        employee.setFullName("John Doe");
        employee.setCompany("Acme Corporation");
        employee.setPosition("Software Engineer");
        employee.setGender("Male");
        employee.setEmail("john.doe@example.com");
        employeeRepository.save(employee);

        reimbursement = new Reimbursement();
        reimbursement.setStatus(false);
        reimbursement.setEmployee(employee);
        reimbursement.setAmount(BigDecimal.valueOf(1000.00));
        reimbursement.setActivity("Travel");
        reimbursement.setTypeReimbursement("Transport");
        reimbursement.setDescription("Travel to client location");
        reimbursement.setDateCreated(LocalDateTime.now());
        reimbursementRepository.save(reimbursement);
    }

//    @AfterEach
//    public void tearDown() {
//        reimbursementRepository.deleteAll();
//        employeeRepository.deleteAll();
//    }

    @Test
    public void testFindFirstByEmployeeAndReimbursementIdWhenEntityExistsThenReturnOptionalWithEntity() {
        Optional<Reimbursement> foundReimbursement = reimbursementRepository.findFirstByEmployeeAndReimbursementId(employee, reimbursement.getReimbursementId());

        assertTrue(foundReimbursement.isPresent());
        assertEquals(reimbursement.getReimbursementId(), foundReimbursement.get().getReimbursementId());
    }

    @Test
    public void testFindFirstByEmployeeAndReimbursementIdWhenEntityDoesNotExistThenReturnEmptyOptional() {
        Optional<Reimbursement> foundReimbursement = reimbursementRepository.findFirstByEmployeeAndReimbursementId(employee, -1L);

        assertFalse(foundReimbursement.isPresent());
    }

    @Test
    public void testFindByStatusFalseWhenEntitiesWithStatusFalseExistThenReturnListWithEntities() {
        List<Reimbursement> foundReimbursements = reimbursementRepository.findByStatusFalse();

        assertEquals(1, foundReimbursements.size());
        assertFalse(foundReimbursements.isEmpty());
        assertTrue(foundReimbursements.stream().noneMatch(Reimbursement::getStatus));
    }

    @Test
    public void testFindByStatusFalseWhenNoEntitiesWithStatusFalseThenReturnEmptyList() {
        reimbursement.setStatus(true);
        reimbursementRepository.save(reimbursement);

        List<Reimbursement> foundReimbursements = reimbursementRepository.findByStatusFalse();

        assertTrue(foundReimbursements.isEmpty());
    }
}
