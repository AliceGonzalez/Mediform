package mediformapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import mediformapp.domain.ChildData;
import mediformapp.repository.ChildDataRepository;
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
 * REST controller for managing {@link mediformapp.domain.ChildData}.
 */
@RestController
@RequestMapping("/api/child-data")
@Transactional
public class ChildDataResource {

    private final Logger log = LoggerFactory.getLogger(ChildDataResource.class);

    private static final String ENTITY_NAME = "childData";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChildDataRepository childDataRepository;

    public ChildDataResource(ChildDataRepository childDataRepository) {
        this.childDataRepository = childDataRepository;
    }

    /**
     * {@code POST  /child-data} : Create a new childData.
     *
     * @param childData the childData to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new childData, or with status {@code 400 (Bad Request)} if the childData has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ChildData> createChildData(@RequestBody ChildData childData) throws URISyntaxException {
        log.debug("REST request to save ChildData : {}", childData);
        if (childData.getId() != null) {
            throw new BadRequestAlertException("A new childData cannot already have an ID", ENTITY_NAME, "idexists");
        }
        childData = childDataRepository.save(childData);
        return ResponseEntity.created(new URI("/api/child-data/" + childData.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, childData.getId().toString()))
            .body(childData);
    }

    /**
     * {@code PUT  /child-data/:id} : Updates an existing childData.
     *
     * @param id the id of the childData to save.
     * @param childData the childData to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated childData,
     * or with status {@code 400 (Bad Request)} if the childData is not valid,
     * or with status {@code 500 (Internal Server Error)} if the childData couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChildData> updateChildData(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ChildData childData
    ) throws URISyntaxException {
        log.debug("REST request to update ChildData : {}, {}", id, childData);
        if (childData.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, childData.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!childDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        childData = childDataRepository.save(childData);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, childData.getId().toString()))
            .body(childData);
    }

    /**
     * {@code PATCH  /child-data/:id} : Partial updates given fields of an existing childData, field will ignore if it is null
     *
     * @param id the id of the childData to save.
     * @param childData the childData to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated childData,
     * or with status {@code 400 (Bad Request)} if the childData is not valid,
     * or with status {@code 404 (Not Found)} if the childData is not found,
     * or with status {@code 500 (Internal Server Error)} if the childData couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ChildData> partialUpdateChildData(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ChildData childData
    ) throws URISyntaxException {
        log.debug("REST request to partial update ChildData partially : {}, {}", id, childData);
        if (childData.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, childData.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!childDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ChildData> result = childDataRepository
            .findById(childData.getId())
            .map(existingChildData -> {
                if (childData.getChildDataID() != null) {
                    existingChildData.setChildDataID(childData.getChildDataID());
                }
                if (childData.getName() != null) {
                    existingChildData.setName(childData.getName());
                }
                if (childData.getLastName() != null) {
                    existingChildData.setLastName(childData.getLastName());
                }
                if (childData.getDob() != null) {
                    existingChildData.setDob(childData.getDob());
                }

                return existingChildData;
            })
            .map(childDataRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, childData.getId().toString())
        );
    }

    /**
     * {@code GET  /child-data} : get all the childData.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of childData in body.
     */
    @GetMapping("")
    public List<ChildData> getAllChildData() {
        log.debug("REST request to get all ChildData");
        return childDataRepository.findAll();
    }

    /**
     * {@code GET  /child-data/:id} : get the "id" childData.
     *
     * @param id the id of the childData to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the childData, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChildData> getChildData(@PathVariable("id") Long id) {
        log.debug("REST request to get ChildData : {}", id);
        Optional<ChildData> childData = childDataRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(childData);
    }

    /**
     * {@code DELETE  /child-data/:id} : delete the "id" childData.
     *
     * @param id the id of the childData to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChildData(@PathVariable("id") Long id) {
        log.debug("REST request to delete ChildData : {}", id);
        childDataRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
