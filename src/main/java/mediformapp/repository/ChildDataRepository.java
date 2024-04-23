package mediformapp.repository;

import mediformapp.domain.ChildData;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ChildData entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChildDataRepository extends JpaRepository<ChildData, Long> {}
