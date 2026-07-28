package yogaharis.restful.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yogaharis.restful.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
}
