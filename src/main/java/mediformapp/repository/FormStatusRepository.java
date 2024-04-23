package mediformapp.repository;

import mediformapp.domain.FormStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FormStatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FormStatusRepository extends JpaRepository<FormStatus, Long> {}
