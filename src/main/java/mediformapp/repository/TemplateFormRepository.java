package mediformapp.repository;

import mediformapp.domain.TemplateForm;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TemplateForm entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TemplateFormRepository extends JpaRepository<TemplateForm, Long> {}
