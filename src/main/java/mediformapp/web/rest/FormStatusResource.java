package mediformapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import mediformapp.domain.FormStatus;
import mediformapp.repository.FormStatusRepository;
import mediformapp.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link mediformapp.domain.FormStatus}.
 */
@RestController
@RequestMapping("/api/form-statuses")
@Transactional
public class FormStatusResource {

    private final Logger log = LoggerFactory.getLogger(FormStatusResource.class);

    private static final String ENTITY_NAME = "formStatus";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FormStatusRepository formStatusRepository;

    public FormStatusResource(FormStatusRepository formStatusRepository) {
        this.formStatusRepository = formStatusRepository;
    }

    /**
     * {@code POST  /form-statuses} : Create a new formStatus.
     *
     * @param formStatus the formStatus to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new formStatus, or with status {@code 400 (Bad Request)} if the formStatus has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FormStatus> createFormStatus(@RequestBody FormStatus formStatus) throws URISyntaxException {
        log.debug("REST request to save FormStatus : {}", formStatus);
        if (formStatus.getId() != null) {
            throw new BadRequestAlertException("A new formStatus cannot already have an ID", ENTITY_NAME, "idexists");
        }
        formStatus = formStatusRepository.save(formStatus);
        return ResponseEntity.created(new URI("/api/form-statuses/" + formStatus.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, formStatus.getId().toString()))
            .body(formStatus);
    }

    /**
     * {@code PUT  /form-statuses/:id} : Updates an existing formStatus.
     *
     * @param id the id of the formStatus to save.
     * @param formStatus the formStatus to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated formStatus,
     * or with status {@code 400 (Bad Request)} if the formStatus is not valid,
     * or with status {@code 500 (Internal Server Error)} if the formStatus couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FormStatus> updateFormStatus(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FormStatus formStatus
    ) throws URISyntaxException {
        log.debug("REST request to update FormStatus : {}, {}", id, formStatus);
        if (formStatus.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, formStatus.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!formStatusRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        formStatus = formStatusRepository.save(formStatus);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, formStatus.getId().toString()))
            .body(formStatus);
    }

    /**
     * {@code PATCH  /form-statuses/:id} : Partial updates given fields of an existing formStatus, field will ignore if it is null
     *
     * @param id the id of the formStatus to save.
     * @param formStatus the formStatus to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated formStatus,
     * or with status {@code 400 (Bad Request)} if the formStatus is not valid,
     * or with status {@code 404 (Not Found)} if the formStatus is not found,
     * or with status {@code 500 (Internal Server Error)} if the formStatus couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FormStatus> partialUpdateFormStatus(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FormStatus formStatus
    ) throws URISyntaxException {
        log.debug("REST request to partial update FormStatus partially : {}, {}", id, formStatus);
        if (formStatus.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, formStatus.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!formStatusRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FormStatus> result = formStatusRepository
            .findById(formStatus.getId())
            .map(existingFormStatus -> {
                if (formStatus.getFormStatusID() != null) {
                    existingFormStatus.setFormStatusID(formStatus.getFormStatusID());
                }
                if (formStatus.getStatus() != null) {
                    existingFormStatus.setStatus(formStatus.getStatus());
                }

                return existingFormStatus;
            })
            .map(formStatusRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, formStatus.getId().toString())
        );
    }

    /**
     * {@code GET  /form-statuses} : get all the formStatuses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of formStatuses in body.
     */
    @GetMapping("")
    public List<FormStatus> getAllFormStatuses() {
        log.debug("REST request to get all FormStatuses");
        return formStatusRepository.findAll();
    }

    /**
     * {@code GET  /form-statuses/:id} : get the "id" formStatus.
     *
     * @param id the id of the formStatus to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the formStatus, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FormStatus> getFormStatus(@PathVariable("id") Long id) {
        log.debug("REST request to get FormStatus : {}", id);
        Optional<FormStatus> formStatus = formStatusRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(formStatus);
    }

    /**
     * {@code DELETE  /form-statuses/:id} : delete the "id" formStatus.
     *
     * @param id the id of the formStatus to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFormStatus(@PathVariable("id") Long id) {
        log.debug("REST request to delete FormStatus : {}", id);
        formStatusRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
