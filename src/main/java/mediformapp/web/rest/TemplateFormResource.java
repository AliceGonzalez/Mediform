package mediformapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import mediformapp.domain.TemplateForm;
import mediformapp.repository.TemplateFormRepository;
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
 * REST controller for managing {@link mediformapp.domain.TemplateForm}.
 */
@RestController
@RequestMapping("/api/template-forms")
@Transactional
public class TemplateFormResource {

    private final Logger log = LoggerFactory.getLogger(TemplateFormResource.class);

    private static final String ENTITY_NAME = "templateForm";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TemplateFormRepository templateFormRepository;

    public TemplateFormResource(TemplateFormRepository templateFormRepository) {
        this.templateFormRepository = templateFormRepository;
    }

    /**
     * {@code POST  /template-forms} : Create a new templateForm.
     *
     * @param templateForm the templateForm to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new templateForm, or with status {@code 400 (Bad Request)} if the templateForm has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TemplateForm> createTemplateForm(@RequestBody TemplateForm templateForm) throws URISyntaxException {
        log.debug("REST request to save TemplateForm : {}", templateForm);
        if (templateForm.getId() != null) {
            throw new BadRequestAlertException("A new templateForm cannot already have an ID", ENTITY_NAME, "idexists");
        }
        templateForm = templateFormRepository.save(templateForm);
        return ResponseEntity.created(new URI("/api/template-forms/" + templateForm.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, templateForm.getId().toString()))
            .body(templateForm);
    }

    /**
     * {@code PUT  /template-forms/:id} : Updates an existing templateForm.
     *
     * @param id the id of the templateForm to save.
     * @param templateForm the templateForm to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated templateForm,
     * or with status {@code 400 (Bad Request)} if the templateForm is not valid,
     * or with status {@code 500 (Internal Server Error)} if the templateForm couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TemplateForm> updateTemplateForm(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TemplateForm templateForm
    ) throws URISyntaxException {
        log.debug("REST request to update TemplateForm : {}, {}", id, templateForm);
        if (templateForm.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, templateForm.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!templateFormRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        templateForm = templateFormRepository.save(templateForm);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, templateForm.getId().toString()))
            .body(templateForm);
    }

    /**
     * {@code PATCH  /template-forms/:id} : Partial updates given fields of an existing templateForm, field will ignore if it is null
     *
     * @param id the id of the templateForm to save.
     * @param templateForm the templateForm to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated templateForm,
     * or with status {@code 400 (Bad Request)} if the templateForm is not valid,
     * or with status {@code 404 (Not Found)} if the templateForm is not found,
     * or with status {@code 500 (Internal Server Error)} if the templateForm couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TemplateForm> partialUpdateTemplateForm(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TemplateForm templateForm
    ) throws URISyntaxException {
        log.debug("REST request to partial update TemplateForm partially : {}, {}", id, templateForm);
        if (templateForm.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, templateForm.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!templateFormRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TemplateForm> result = templateFormRepository
            .findById(templateForm.getId())
            .map(existingTemplateForm -> {
                if (templateForm.getTemplateFormID() != null) {
                    existingTemplateForm.setTemplateFormID(templateForm.getTemplateFormID());
                }
                if (templateForm.getFormType() != null) {
                    existingTemplateForm.setFormType(templateForm.getFormType());
                }

                return existingTemplateForm;
            })
            .map(templateFormRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, templateForm.getId().toString())
        );
    }

    /**
     * {@code GET  /template-forms} : get all the templateForms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of templateForms in body.
     */
    @GetMapping("")
    public List<TemplateForm> getAllTemplateForms() {
        log.debug("REST request to get all TemplateForms");
        return templateFormRepository.findAll();
    }

    /**
     * {@code GET  /template-forms/:id} : get the "id" templateForm.
     *
     * @param id the id of the templateForm to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the templateForm, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TemplateForm> getTemplateForm(@PathVariable("id") Long id) {
        log.debug("REST request to get TemplateForm : {}", id);
        Optional<TemplateForm> templateForm = templateFormRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(templateForm);
    }

    /**
     * {@code DELETE  /template-forms/:id} : delete the "id" templateForm.
     *
     * @param id the id of the templateForm to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplateForm(@PathVariable("id") Long id) {
        log.debug("REST request to delete TemplateForm : {}", id);
        templateFormRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
