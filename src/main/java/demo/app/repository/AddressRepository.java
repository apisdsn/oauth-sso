package demo.app.repository;

import demo.app.entity.Address;
import demo.app.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, String>, JpaSpecificationExecutor<Address> {
}
