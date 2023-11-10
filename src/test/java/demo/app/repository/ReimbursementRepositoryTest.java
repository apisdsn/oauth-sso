package demo.app.repository;

import demo.app.entity.Employee;
import demo.app.entity.Reimbursement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ReimbursementRepositoryTest {

    @Autowired
    private ReimbursementRepository reimbursementRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;
    private Reimbursement reimbursement;

    @BeforeEach
    public void setUp() {
        employee = new Employee();
        employee.setFullName("John Doe");
        employee.setEmail("john.doe@example.com");
        employee = employeeRepository.save(employee);

        reimbursement = new Reimbursement();
        reimbursement.setEmployee(employee);
        reimbursement.setStatus(false);
        reimbursement = reimbursementRepository.save(reimbursement);
    }

    @AfterEach
    public void tearDown() {
        reimbursementRepository.deleteAll();
        employeeRepository.deleteAll();
    }

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

        assertFalse(foundReimbursements.isEmpty());
        assertTrue(foundReimbursements.stream().allMatch(reimbursement -> !reimbursement.getStatus()));
    }

    @Test
    public void testFindByStatusFalseWhenNoEntitiesWithStatusFalseThenReturnEmptyList() {
        reimbursement.setStatus(true);
        reimbursementRepository.save(reimbursement);

        List<Reimbursement> foundReimbursements = reimbursementRepository.findByStatusFalse();

        assertTrue(foundReimbursements.isEmpty());
    }
}
