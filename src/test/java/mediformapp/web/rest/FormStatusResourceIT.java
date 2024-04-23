package mediformapp.web.rest;

import static mediformapp.domain.FormStatusAsserts.*;
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
import mediformapp.domain.FormStatus;
import mediformapp.repository.FormStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FormStatusResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FormStatusResourceIT {

    private static final Integer DEFAULT_FORM_STATUS_ID = 1;
    private static final Integer UPDATED_FORM_STATUS_ID = 2;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/form-statuses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FormStatusRepository formStatusRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFormStatusMockMvc;

    private FormStatus formStatus;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FormStatus createEntity(EntityManager em) {
        FormStatus formStatus = new FormStatus().formStatusID(DEFAULT_FORM_STATUS_ID).status(DEFAULT_STATUS);
        return formStatus;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FormStatus createUpdatedEntity(EntityManager em) {
        FormStatus formStatus = new FormStatus().formStatusID(UPDATED_FORM_STATUS_ID).status(UPDATED_STATUS);
        return formStatus;
    }

    @BeforeEach
    public void initTest() {
        formStatus = createEntity(em);
    }

    @Test
    @Transactional
    void createFormStatus() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FormStatus
        var returnedFormStatus = om.readValue(
            restFormStatusMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(formStatus)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FormStatus.class
        );

        // Validate the FormStatus in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertFormStatusUpdatableFieldsEquals(returnedFormStatus, getPersistedFormStatus(returnedFormStatus));
    }

    @Test
    @Transactional
    void createFormStatusWithExistingId() throws Exception {
        // Create the FormStatus with an existing ID
        formStatus.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFormStatusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(formStatus)))
            .andExpect(status().isBadRequest());

        // Validate the FormStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFormStatuses() throws Exception {
        // Initialize the database
        formStatusRepository.saveAndFlush(formStatus);

        // Get all the formStatusList
        restFormStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(formStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].formStatusID").value(hasItem(DEFAULT_FORM_STATUS_ID)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    void getFormStatus() throws Exception {
        // Initialize the database
        formStatusRepository.saveAndFlush(formStatus);

        // Get the formStatus
        restFormStatusMockMvc
            .perform(get(ENTITY_API_URL_ID, formStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(formStatus.getId().intValue()))
            .andExpect(jsonPath("$.formStatusID").value(DEFAULT_FORM_STATUS_ID))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getNonExistingFormStatus() throws Exception {
        // Get the formStatus
        restFormStatusMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFormStatus() throws Exception {
        // Initialize the database
        formStatusRepository.saveAndFlush(formStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the formStatus
        FormStatus updatedFormStatus = formStatusRepository.findById(formStatus.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFormStatus are not directly saved in db
        em.detach(updatedFormStatus);
        updatedFormStatus.formStatusID(UPDATED_FORM_STATUS_ID).status(UPDATED_STATUS);

        restFormStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFormStatus.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedFormStatus))
            )
            .andExpect(status().isOk());

        // Validate the FormStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFormStatusToMatchAllProperties(updatedFormStatus);
    }

    @Test
    @Transactional
    void putNonExistingFormStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        formStatus.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFormStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, formStatus.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(formStatus))
            )
            .andExpect(status().isBadRequest());

        // Validate the FormStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFormStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        formStatus.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFormStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(formStatus))
            )
            .andExpect(status().isBadRequest());

        // Validate the FormStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFormStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        formStatus.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFormStatusMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(formStatus)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FormStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFormStatusWithPatch() throws Exception {
        // Initialize the database
        formStatusRepository.saveAndFlush(formStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the formStatus using partial update
        FormStatus partialUpdatedFormStatus = new FormStatus();
        partialUpdatedFormStatus.setId(formStatus.getId());

        partialUpdatedFormStatus.status(UPDATED_STATUS);

        restFormStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFormStatus.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFormStatus))
            )
            .andExpect(status().isOk());

        // Validate the FormStatus in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFormStatusUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFormStatus, formStatus),
            getPersistedFormStatus(formStatus)
        );
    }

    @Test
    @Transactional
    void fullUpdateFormStatusWithPatch() throws Exception {
        // Initialize the database
        formStatusRepository.saveAndFlush(formStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the formStatus using partial update
        FormStatus partialUpdatedFormStatus = new FormStatus();
        partialUpdatedFormStatus.setId(formStatus.getId());

        partialUpdatedFormStatus.formStatusID(UPDATED_FORM_STATUS_ID).status(UPDATED_STATUS);

        restFormStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFormStatus.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFormStatus))
            )
            .andExpect(status().isOk());

        // Validate the FormStatus in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFormStatusUpdatableFieldsEquals(partialUpdatedFormStatus, getPersistedFormStatus(partialUpdatedFormStatus));
    }

    @Test
    @Transactional
    void patchNonExistingFormStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        formStatus.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFormStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, formStatus.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(formStatus))
            )
            .andExpect(status().isBadRequest());

        // Validate the FormStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFormStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        formStatus.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFormStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(formStatus))
            )
            .andExpect(status().isBadRequest());

        // Validate the FormStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFormStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        formStatus.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFormStatusMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(formStatus)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FormStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFormStatus() throws Exception {
        // Initialize the database
        formStatusRepository.saveAndFlush(formStatus);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the formStatus
        restFormStatusMockMvc
            .perform(delete(ENTITY_API_URL_ID, formStatus.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return formStatusRepository.count();
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

    protected FormStatus getPersistedFormStatus(FormStatus formStatus) {
        return formStatusRepository.findById(formStatus.getId()).orElseThrow();
    }

    protected void assertPersistedFormStatusToMatchAllProperties(FormStatus expectedFormStatus) {
        assertFormStatusAllPropertiesEquals(expectedFormStatus, getPersistedFormStatus(expectedFormStatus));
    }

    protected void assertPersistedFormStatusToMatchUpdatableProperties(FormStatus expectedFormStatus) {
        assertFormStatusAllUpdatablePropertiesEquals(expectedFormStatus, getPersistedFormStatus(expectedFormStatus));
    }
}
