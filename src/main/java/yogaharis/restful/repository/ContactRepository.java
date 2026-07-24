package yogaharis.restful.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yogaharis.restful.entity.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {
}
