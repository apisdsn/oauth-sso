package demo.app.repository;

import demo.app.entity.Employee;
import demo.app.entity.Reimbursement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReimbursementRepositoryTest {
    @Mock
    private ReimbursementRepository reimbursementRepository;
    private Employee employee;
    private Reimbursement reimbursement;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employee = new Employee();
        employee.setClientId("123");
        employee.setFullName("John Doe");
        employee.setEmail("john.doe@example.com");

        reimbursement = new Reimbursement();
        reimbursement.setEmployee(employee);
        reimbursement.setAmount(new BigDecimal("100.00"));
        reimbursement.setStatus(false);
        reimbursement.setActivity("Travel");

        // Mock the behavior of the repositories
        when(reimbursementRepository.save(any(Reimbursement.class))).thenReturn(reimbursement);
        when(reimbursementRepository.findFirstByEmployeeAndReimbursementId(eq(employee), anyLong())).thenReturn(Optional.of(reimbursement));
    }

    @AfterEach
    void tearDown() {
        reset(reimbursementRepository);
    }

    @Test
    void testFindFirstByEmployeeAndReimbursementIdWhenExistsThenReturnReimbursement() {
        when(reimbursementRepository.findFirstByEmployeeAndReimbursementId(employee, reimbursement.getReimbursementId())).thenReturn(Optional.of(reimbursement));
        Optional<Reimbursement> foundReimbursement = reimbursementRepository.findFirstByEmployeeAndReimbursementId(employee, reimbursement.getReimbursementId());

        assertTrue(foundReimbursement.isPresent());
        assertEquals(reimbursement.getReimbursementId(), foundReimbursement.get().getReimbursementId());
    }

    @Test
    void testFindFirstByEmployeeAndReimbursementIdWhenNonExistentIdThenReturnEmptyOptional() {
        when(reimbursementRepository.findFirstByEmployeeAndReimbursementId(employee, Long.MAX_VALUE)).thenReturn(Optional.empty());
        Optional<Reimbursement> foundReimbursement = reimbursementRepository.findFirstByEmployeeAndReimbursementId(employee, Long.MAX_VALUE);

        assertFalse(foundReimbursement.isPresent());
    }

    @Test
    void testFindFirstByEmployeeAndReimbursementIdWhenNonExistentEmployeeThenReturnEmptyOptional() {
        Employee nonExistentEmployee = new Employee();
        nonExistentEmployee.setFullName("Non Existent");
        nonExistentEmployee.setEmail("non.existent@example.com");

        Optional<Reimbursement> foundReimbursement = reimbursementRepository.findFirstByEmployeeAndReimbursementId(nonExistentEmployee, reimbursement.getReimbursementId());

        assertFalse(foundReimbursement.isPresent());
    }

    @Test
    void testFindByStatusFalseWhenExistsThenReturnReimbursements() {
        when(reimbursementRepository.findByStatusFalse()).thenReturn(List.of(reimbursement));

        List<Reimbursement> foundReimbursements = reimbursementRepository.findByStatusFalse();

        assertFalse(foundReimbursements.isEmpty());
        assertTrue(foundReimbursements.contains(reimbursement));
    }

    @Test
    void testFindByStatusFalseWhenNonExistentThenReturnEmptyList() {
        reimbursement.setStatus(true);

        List<Reimbursement> foundReimbursements = reimbursementRepository.findByStatusFalse();

        assertTrue(foundReimbursements.isEmpty());
    }
}