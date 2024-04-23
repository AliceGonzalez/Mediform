package mediformapp.repository;

import mediformapp.domain.Login;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Login entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LoginRepository extends JpaRepository<Login, Long> {}
