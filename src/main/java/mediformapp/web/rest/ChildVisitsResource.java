package mediformapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import mediformapp.domain.ChildVisits;
import mediformapp.repository.ChildVisitsRepository;
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
 * REST controller for managing {@link mediformapp.domain.ChildVisits}.
 */
@RestController
@RequestMapping("/api/child-visits")
@Transactional
public class ChildVisitsResource {

    private final Logger log = LoggerFactory.getLogger(ChildVisitsResource.class);

    private static final String ENTITY_NAME = "childVisits";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChildVisitsRepository childVisitsRepository;

    public ChildVisitsResource(ChildVisitsRepository childVisitsRepository) {
        this.childVisitsRepository = childVisitsRepository;
    }

    /**
     * {@code POST  /child-visits} : Create a new childVisits.
     *
     * @param childVisits the childVisits to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new childVisits, or with status {@code 400 (Bad Request)} if the childVisits has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ChildVisits> createChildVisits(@RequestBody ChildVisits childVisits) throws URISyntaxException {
        log.debug("REST request to save ChildVisits : {}", childVisits);
        if (childVisits.getId() != null) {
            throw new BadRequestAlertException("A new childVisits cannot already have an ID", ENTITY_NAME, "idexists");
        }
        childVisits = childVisitsRepository.save(childVisits);
        return ResponseEntity.created(new URI("/api/child-visits/" + childVisits.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, childVisits.getId().toString()))
            .body(childVisits);
    }

    /**
     * {@code PUT  /child-visits/:id} : Updates an existing childVisits.
     *
     * @param id the id of the childVisits to save.
     * @param childVisits the childVisits to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated childVisits,
     * or with status {@code 400 (Bad Request)} if the childVisits is not valid,
     * or with status {@code 500 (Internal Server Error)} if the childVisits couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChildVisits> updateChildVisits(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ChildVisits childVisits
    ) throws URISyntaxException {
        log.debug("REST request to update ChildVisits : {}, {}", id, childVisits);
        if (childVisits.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, childVisits.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!childVisitsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        childVisits = childVisitsRepository.save(childVisits);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, childVisits.getId().toString()))
            .body(childVisits);
    }

    /**
     * {@code PATCH  /child-visits/:id} : Partial updates given fields of an existing childVisits, field will ignore if it is null
     *
     * @param id the id of the childVisits to save.
     * @param childVisits the childVisits to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated childVisits,
     * or with status {@code 400 (Bad Request)} if the childVisits is not valid,
     * or with status {@code 404 (Not Found)} if the childVisits is not found,
     * or with status {@code 500 (Internal Server Error)} if the childVisits couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ChildVisits> partialUpdateChildVisits(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ChildVisits childVisits
    ) throws URISyntaxException {
        log.debug("REST request to partial update ChildVisits partially : {}, {}", id, childVisits);
        if (childVisits.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, childVisits.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!childVisitsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ChildVisits> result = childVisitsRepository
            .findById(childVisits.getId())
            .map(existingChildVisits -> {
                if (childVisits.getVisitID() != null) {
                    existingChildVisits.setVisitID(childVisits.getVisitID());
                }
                if (childVisits.getVisitType() != null) {
                    existingChildVisits.setVisitType(childVisits.getVisitType());
                }
                if (childVisits.getVisitDate() != null) {
                    existingChildVisits.setVisitDate(childVisits.getVisitDate());
                }

                return existingChildVisits;
            })
            .map(childVisitsRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, childVisits.getId().toString())
        );
    }

    /**
     * {@code GET  /child-visits} : get all the childVisits.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of childVisits in body.
     */
    @GetMapping("")
    public List<ChildVisits> getAllChildVisits() {
        log.debug("REST request to get all ChildVisits");
        return childVisitsRepository.findAll();
    }

    /**
     * {@code GET  /child-visits/:id} : get the "id" childVisits.
     *
     * @param id the id of the childVisits to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the childVisits, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChildVisits> getChildVisits(@PathVariable("id") Long id) {
        log.debug("REST request to get ChildVisits : {}", id);
        Optional<ChildVisits> childVisits = childVisitsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(childVisits);
    }

    /**
     * {@code DELETE  /child-visits/:id} : delete the "id" childVisits.
     *
     * @param id the id of the childVisits to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChildVisits(@PathVariable("id") Long id) {
        log.debug("REST request to delete ChildVisits : {}", id);
        childVisitsRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
