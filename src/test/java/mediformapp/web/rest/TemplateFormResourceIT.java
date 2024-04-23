package mediformapp.web.rest;

import static mediformapp.domain.TemplateFormAsserts.*;
import static mediformapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import mediformapp.IntegrationTest;
import mediformapp.domain.TemplateForm;
import mediformapp.repository.TemplateFormRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TemplateFormResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TemplateFormResourceIT {

    private static final Integer DEFAULT_TEMPLATE_FORM_ID = 1;
    private static final Integer UPDATED_TEMPLATE_FORM_ID = 2;

    private static final String DEFAULT_FORM_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_FORM_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/template-forms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TemplateFormRepository templateFormRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTemplateFormMockMvc;

    private TemplateForm templateForm;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TemplateForm createEntity(EntityManager em) {
        TemplateForm templateForm = new TemplateForm().templateFormID(DEFAULT_TEMPLATE_FORM_ID).formType(DEFAULT_FORM_TYPE);
        return templateForm;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TemplateForm createUpdatedEntity(EntityManager em) {
        TemplateForm templateForm = new TemplateForm().templateFormID(UPDATED_TEMPLATE_FORM_ID).formType(UPDATED_FORM_TYPE);
        return templateForm;
    }

    @BeforeEach
    public void initTest() {
        templateForm = createEntity(em);
    }

    @Test
    @Transactional
    void createTemplateForm() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TemplateForm
        var returnedTemplateForm = om.readValue(
            restTemplateFormMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(templateForm)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TemplateForm.class
        );

        // Validate the TemplateForm in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTemplateFormUpdatableFieldsEquals(returnedTemplateForm, getPersistedTemplateForm(returnedTemplateForm));
    }

    @Test
    @Transactional
    void createTemplateFormWithExistingId() throws Exception {
        // Create the TemplateForm with an existing ID
        templateForm.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTemplateFormMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(templateForm)))
            .andExpect(status().isBadRequest());

        // Validate the TemplateForm in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTemplateForms() throws Exception {
        // Initialize the database
        templateFormRepository.saveAndFlush(templateForm);

        // Get all the templateFormList
        restTemplateFormMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(templateForm.getId().intValue())))
            .andExpect(jsonPath("$.[*].templateFormID").value(hasItem(DEFAULT_TEMPLATE_FORM_ID)))
            .andExpect(jsonPath("$.[*].formType").value(hasItem(DEFAULT_FORM_TYPE)));
    }

    @Test
    @Transactional
    void getTemplateForm() throws Exception {
        // Initialize the database
        templateFormRepository.saveAndFlush(templateForm);

        // Get the templateForm
        restTemplateFormMockMvc
            .perform(get(ENTITY_API_URL_ID, templateForm.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(templateForm.getId().intValue()))
            .andExpect(jsonPath("$.templateFormID").value(DEFAULT_TEMPLATE_FORM_ID))
            .andExpect(jsonPath("$.formType").value(DEFAULT_FORM_TYPE));
    }

    @Test
    @Transactional
    void getNonExistingTemplateForm() throws Exception {
        // Get the templateForm
        restTemplateFormMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTemplateForm() throws Exception {
        // Initialize the database
        templateFormRepository.saveAndFlush(templateForm);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the templateForm
        TemplateForm updatedTemplateForm = templateFormRepository.findById(templateForm.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTemplateForm are not directly saved in db
        em.detach(updatedTemplateForm);
        updatedTemplateForm.templateFormID(UPDATED_TEMPLATE_FORM_ID).formType(UPDATED_FORM_TYPE);

        restTemplateFormMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTemplateForm.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedTemplateForm))
            )
            .andExpect(status().isOk());

        // Validate the TemplateForm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTemplateFormToMatchAllProperties(updatedTemplateForm);
    }

    @Test
    @Transactional
    void putNonExistingTemplateForm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        templateForm.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTemplateFormMockMvc
            .perform(
                put(ENTITY_API_URL_ID, templateForm.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(templateForm))
            )
            .andExpect(status().isBadRequest());

        // Validate the TemplateForm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTemplateForm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        templateForm.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemplateFormMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(templateForm))
            )
            .andExpect(status().isBadRequest());

        // Validate the TemplateForm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTemplateForm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        templateForm.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemplateFormMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(templateForm)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TemplateForm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTemplateFormWithPatch() throws Exception {
        // Initialize the database
        templateFormRepository.saveAndFlush(templateForm);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the templateForm using partial update
        TemplateForm partialUpdatedTemplateForm = new TemplateForm();
        partialUpdatedTemplateForm.setId(templateForm.getId());

        partialUpdatedTemplateForm.templateFormID(UPDATED_TEMPLATE_FORM_ID);

        restTemplateFormMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTemplateForm.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTemplateForm))
            )
            .andExpect(status().isOk());

        // Validate the TemplateForm in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTemplateFormUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTemplateForm, templateForm),
            getPersistedTemplateForm(templateForm)
        );
    }

    @Test
    @Transactional
    void fullUpdateTemplateFormWithPatch() throws Exception {
        // Initialize the database
        templateFormRepository.saveAndFlush(templateForm);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the templateForm using partial update
        TemplateForm partialUpdatedTemplateForm = new TemplateForm();
        partialUpdatedTemplateForm.setId(templateForm.getId());

        partialUpdatedTemplateForm.templateFormID(UPDATED_TEMPLATE_FORM_ID).formType(UPDATED_FORM_TYPE);

        restTemplateFormMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTemplateForm.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTemplateForm))
            )
            .andExpect(status().isOk());

        // Validate the TemplateForm in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTemplateFormUpdatableFieldsEquals(partialUpdatedTemplateForm, getPersistedTemplateForm(partialUpdatedTemplateForm));
    }

    @Test
    @Transactional
    void patchNonExistingTemplateForm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        templateForm.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTemplateFormMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, templateForm.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(templateForm))
            )
            .andExpect(status().isBadRequest());

        // Validate the TemplateForm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTemplateForm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        templateForm.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemplateFormMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(templateForm))
            )
            .andExpect(status().isBadRequest());

        // Validate the TemplateForm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTemplateForm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        templateForm.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemplateFormMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(templateForm)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TemplateForm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTemplateForm() throws Exception {
        // Initialize the database
        templateFormRepository.saveAndFlush(templateForm);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the templateForm
        restTemplateFormMockMvc
            .perform(delete(ENTITY_API_URL_ID, templateForm.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return templateFormRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected TemplateForm getPersistedTemplateForm(TemplateForm templateForm) {
        return templateFormRepository.findById(templateForm.getId()).orElseThrow();
    }

    protected void assertPersistedTemplateFormToMatchAllProperties(TemplateForm expectedTemplateForm) {
        assertTemplateFormAllPropertiesEquals(expectedTemplateForm, getPersistedTemplateForm(expectedTemplateForm));
    }

    protected void assertPersistedTemplateFormToMatchUpdatableProperties(TemplateForm expectedTemplateForm) {
        assertTemplateFormAllUpdatablePropertiesEquals(expectedTemplateForm, getPersistedTemplateForm(expectedTemplateForm));
    }
}
