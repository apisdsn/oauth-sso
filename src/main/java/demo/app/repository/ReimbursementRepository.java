package demo.app.repository;

import demo.app.entity.Employee;
import demo.app.entity.Reimbursement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReimbursementRepository extends JpaRepository<Reimbursement, String> {
    Optional<Reimbursement> findByEmployee(Employee employee);

    List<Reimbursement> findByStatusFalse();
}
