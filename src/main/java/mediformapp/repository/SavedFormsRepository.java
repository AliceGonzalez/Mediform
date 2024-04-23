package mediformapp.repository;

import mediformapp.domain.SavedForms;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SavedForms entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SavedFormsRepository extends JpaRepository<SavedForms, Long> {}
