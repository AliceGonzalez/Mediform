package mediformapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import mediformapp.domain.SavedForms;
import mediformapp.repository.SavedFormsRepository;
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
 * REST controller for managing {@link mediformapp.domain.SavedForms}.
 */
@RestController
@RequestMapping("/api/saved-forms")
@Transactional
public class SavedFormsResource {

    private final Logger log = LoggerFactory.getLogger(SavedFormsResource.class);

    private static final String ENTITY_NAME = "savedForms";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SavedFormsRepository savedFormsRepository;

    public SavedFormsResource(SavedFormsRepository savedFormsRepository) {
        this.savedFormsRepository = savedFormsRepository;
    }

    /**
     * {@code POST  /saved-forms} : Create a new savedForms.
     *
     * @param savedForms the savedForms to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new savedForms, or with status {@code 400 (Bad Request)} if the savedForms has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SavedForms> createSavedForms(@RequestBody SavedForms savedForms) throws URISyntaxException {
        log.debug("REST request to save SavedForms : {}", savedForms);
        if (savedForms.getId() != null) {
            throw new BadRequestAlertException("A new savedForms cannot already have an ID", ENTITY_NAME, "idexists");
        }
        savedForms = savedFormsRepository.save(savedForms);
        return ResponseEntity.created(new URI("/api/saved-forms/" + savedForms.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, savedForms.getId().toString()))
            .body(savedForms);
    }

    /**
     * {@code PUT  /saved-forms/:id} : Updates an existing savedForms.
     *
     * @param id the id of the savedForms to save.
     * @param savedForms the savedForms to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated savedForms,
     * or with status {@code 400 (Bad Request)} if the savedForms is not valid,
     * or with status {@code 500 (Internal Server Error)} if the savedForms couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SavedForms> updateSavedForms(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SavedForms savedForms
    ) throws URISyntaxException {
        log.debug("REST request to update SavedForms : {}, {}", id, savedForms);
        if (savedForms.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, savedForms.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!savedFormsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        savedForms = savedFormsRepository.save(savedForms);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, savedForms.getId().toString()))
            .body(savedForms);
    }

    /**
     * {@code PATCH  /saved-forms/:id} : Partial updates given fields of an existing savedForms, field will ignore if it is null
     *
     * @param id the id of the savedForms to save.
     * @param savedForms the savedForms to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated savedForms,
     * or with status {@code 400 (Bad Request)} if the savedForms is not valid,
     * or with status {@code 404 (Not Found)} if the savedForms is not found,
     * or with status {@code 500 (Internal Server Error)} if the savedForms couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SavedForms> partialUpdateSavedForms(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SavedForms savedForms
    ) throws URISyntaxException {
        log.debug("REST request to partial update SavedForms partially : {}, {}", id, savedForms);
        if (savedForms.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, savedForms.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!savedFormsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SavedForms> result = savedFormsRepository
            .findById(savedForms.getId())
            .map(existingSavedForms -> {
                if (savedForms.getSavedFormID() != null) {
                    existingSavedForms.setSavedFormID(savedForms.getSavedFormID());
                }
                if (savedForms.getFormID() != null) {
                    existingSavedForms.setFormID(savedForms.getFormID());
                }
                if (savedForms.getFormType() != null) {
                    existingSavedForms.setFormType(savedForms.getFormType());
                }

                return existingSavedForms;
            })
            .map(savedFormsRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, savedForms.getId().toString())
        );
    }

    /**
     * {@code GET  /saved-forms} : get all the savedForms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of savedForms in body.
     */
    @GetMapping("")
    public List<SavedForms> getAllSavedForms() {
        log.debug("REST request to get all SavedForms");
        return savedFormsRepository.findAll();
    }

    /**
     * {@code GET  /saved-forms/:id} : get the "id" savedForms.
     *
     * @param id the id of the savedForms to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the savedForms, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SavedForms> getSavedForms(@PathVariable("id") Long id) {
        log.debug("REST request to get SavedForms : {}", id);
        Optional<SavedForms> savedForms = savedFormsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(savedForms);
    }

    /**
     * {@code DELETE  /saved-forms/:id} : delete the "id" savedForms.
     *
     * @param id the id of the savedForms to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSavedForms(@PathVariable("id") Long id) {
        log.debug("REST request to delete SavedForms : {}", id);
        savedFormsRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
