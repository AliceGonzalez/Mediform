package mediformapp.web.rest;

import static mediformapp.domain.SavedFormsAsserts.*;
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
import mediformapp.domain.SavedForms;
import mediformapp.repository.SavedFormsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SavedFormsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SavedFormsResourceIT {

    private static final Integer DEFAULT_SAVED_FORM_ID = 1;
    private static final Integer UPDATED_SAVED_FORM_ID = 2;

    private static final Integer DEFAULT_FORM_ID = 1;
    private static final Integer UPDATED_FORM_ID = 2;

    private static final String DEFAULT_FORM_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_FORM_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/saved-forms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SavedFormsRepository savedFormsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSavedFormsMockMvc;

    private SavedForms savedForms;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SavedForms createEntity(EntityManager em) {
        SavedForms savedForms = new SavedForms().savedFormID(DEFAULT_SAVED_FORM_ID).formID(DEFAULT_FORM_ID).formType(DEFAULT_FORM_TYPE);
        return savedForms;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SavedForms createUpdatedEntity(EntityManager em) {
        SavedForms savedForms = new SavedForms().savedFormID(UPDATED_SAVED_FORM_ID).formID(UPDATED_FORM_ID).formType(UPDATED_FORM_TYPE);
        return savedForms;
    }

    @BeforeEach
    public void initTest() {
        savedForms = createEntity(em);
    }

    @Test
    @Transactional
    void createSavedForms() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SavedForms
        var returnedSavedForms = om.readValue(
            restSavedFormsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(savedForms)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SavedForms.class
        );

        // Validate the SavedForms in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSavedFormsUpdatableFieldsEquals(returnedSavedForms, getPersistedSavedForms(returnedSavedForms));
    }

    @Test
    @Transactional
    void createSavedFormsWithExistingId() throws Exception {
        // Create the SavedForms with an existing ID
        savedForms.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSavedFormsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(savedForms)))
            .andExpect(status().isBadRequest());

        // Validate the SavedForms in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSavedForms() throws Exception {
        // Initialize the database
        savedFormsRepository.saveAndFlush(savedForms);

        // Get all the savedFormsList
        restSavedFormsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(savedForms.getId().intValue())))
            .andExpect(jsonPath("$.[*].savedFormID").value(hasItem(DEFAULT_SAVED_FORM_ID)))
            .andExpect(jsonPath("$.[*].formID").value(hasItem(DEFAULT_FORM_ID)))
            .andExpect(jsonPath("$.[*].formType").value(hasItem(DEFAULT_FORM_TYPE)));
    }

    @Test
    @Transactional
    void getSavedForms() throws Exception {
        // Initialize the database
        savedFormsRepository.saveAndFlush(savedForms);

        // Get the savedForms
        restSavedFormsMockMvc
            .perform(get(ENTITY_API_URL_ID, savedForms.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(savedForms.getId().intValue()))
            .andExpect(jsonPath("$.savedFormID").value(DEFAULT_SAVED_FORM_ID))
            .andExpect(jsonPath("$.formID").value(DEFAULT_FORM_ID))
            .andExpect(jsonPath("$.formType").value(DEFAULT_FORM_TYPE));
    }

    @Test
    @Transactional
    void getNonExistingSavedForms() throws Exception {
        // Get the savedForms
        restSavedFormsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSavedForms() throws Exception {
        // Initialize the database
        savedFormsRepository.saveAndFlush(savedForms);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the savedForms
        SavedForms updatedSavedForms = savedFormsRepository.findById(savedForms.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSavedForms are not directly saved in db
        em.detach(updatedSavedForms);
        updatedSavedForms.savedFormID(UPDATED_SAVED_FORM_ID).formID(UPDATED_FORM_ID).formType(UPDATED_FORM_TYPE);

        restSavedFormsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSavedForms.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSavedForms))
            )
            .andExpect(status().isOk());

        // Validate the SavedForms in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSavedFormsToMatchAllProperties(updatedSavedForms);
    }

    @Test
    @Transactional
    void putNonExistingSavedForms() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        savedForms.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSavedFormsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, savedForms.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(savedForms))
            )
            .andExpect(status().isBadRequest());

        // Validate the SavedForms in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSavedForms() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        savedForms.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSavedFormsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(savedForms))
            )
            .andExpect(status().isBadRequest());

        // Validate the SavedForms in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSavedForms() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        savedForms.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSavedFormsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(savedForms)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SavedForms in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSavedFormsWithPatch() throws Exception {
        // Initialize the database
        savedFormsRepository.saveAndFlush(savedForms);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the savedForms using partial update
        SavedForms partialUpdatedSavedForms = new SavedForms();
        partialUpdatedSavedForms.setId(savedForms.getId());

        partialUpdatedSavedForms.formID(UPDATED_FORM_ID).formType(UPDATED_FORM_TYPE);

        restSavedFormsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSavedForms.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSavedForms))
            )
            .andExpect(status().isOk());

        // Validate the SavedForms in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSavedFormsUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSavedForms, savedForms),
            getPersistedSavedForms(savedForms)
        );
    }

    @Test
    @Transactional
    void fullUpdateSavedFormsWithPatch() throws Exception {
        // Initialize the database
        savedFormsRepository.saveAndFlush(savedForms);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the savedForms using partial update
        SavedForms partialUpdatedSavedForms = new SavedForms();
        partialUpdatedSavedForms.setId(savedForms.getId());

        partialUpdatedSavedForms.savedFormID(UPDATED_SAVED_FORM_ID).formID(UPDATED_FORM_ID).formType(UPDATED_FORM_TYPE);

        restSavedFormsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSavedForms.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSavedForms))
            )
            .andExpect(status().isOk());

        // Validate the SavedForms in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSavedFormsUpdatableFieldsEquals(partialUpdatedSavedForms, getPersistedSavedForms(partialUpdatedSavedForms));
    }

    @Test
    @Transactional
    void patchNonExistingSavedForms() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        savedForms.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSavedFormsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, savedForms.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(savedForms))
            )
            .andExpect(status().isBadRequest());

        // Validate the SavedForms in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSavedForms() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        savedForms.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSavedFormsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(savedForms))
            )
            .andExpect(status().isBadRequest());

        // Validate the SavedForms in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSavedForms() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        savedForms.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSavedFormsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(savedForms)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SavedForms in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSavedForms() throws Exception {
        // Initialize the database
        savedFormsRepository.saveAndFlush(savedForms);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the savedForms
        restSavedFormsMockMvc
            .perform(delete(ENTITY_API_URL_ID, savedForms.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return savedFormsRepository.count();
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

    protected SavedForms getPersistedSavedForms(SavedForms savedForms) {
        return savedFormsRepository.findById(savedForms.getId()).orElseThrow();
    }

    protected void assertPersistedSavedFormsToMatchAllProperties(SavedForms expectedSavedForms) {
        assertSavedFormsAllPropertiesEquals(expectedSavedForms, getPersistedSavedForms(expectedSavedForms));
    }

    protected void assertPersistedSavedFormsToMatchUpdatableProperties(SavedForms expectedSavedForms) {
        assertSavedFormsAllUpdatablePropertiesEquals(expectedSavedForms, getPersistedSavedForms(expectedSavedForms));
    }
}
