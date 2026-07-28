package yogaharis.restful.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yogaharis.restful.entity.Address;
import yogaharis.restful.entity.Contact;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {

    Optional<Address> findFirstByContactAndId(Contact contact, String id);
}
