package mediformapp.repository;

import mediformapp.domain.ChildVisits;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ChildVisits entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChildVisitsRepository extends JpaRepository<ChildVisits, Long> {}
