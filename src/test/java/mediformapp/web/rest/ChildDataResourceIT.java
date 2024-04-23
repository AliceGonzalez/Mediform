package mediformapp.web.rest;

import static mediformapp.domain.ChildDataAsserts.*;
import static mediformapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import mediformapp.IntegrationTest;
import mediformapp.domain.ChildData;
import mediformapp.repository.ChildDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ChildDataResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ChildDataResourceIT {

    private static final Integer DEFAULT_CHILD_DATA_ID = 1;
    private static final Integer UPDATED_CHILD_DATA_ID = 2;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DOB = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DOB = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/child-data";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ChildDataRepository childDataRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChildDataMockMvc;

    private ChildData childData;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChildData createEntity(EntityManager em) {
        ChildData childData = new ChildData()
            .childDataID(DEFAULT_CHILD_DATA_ID)
            .name(DEFAULT_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .dob(DEFAULT_DOB);
        return childData;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChildData createUpdatedEntity(EntityManager em) {
        ChildData childData = new ChildData()
            .childDataID(UPDATED_CHILD_DATA_ID)
            .name(UPDATED_NAME)
            .lastName(UPDATED_LAST_NAME)
            .dob(UPDATED_DOB);
        return childData;
    }

    @BeforeEach
    public void initTest() {
        childData = createEntity(em);
    }

    @Test
    @Transactional
    void createChildData() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ChildData
        var returnedChildData = om.readValue(
            restChildDataMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(childData)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ChildData.class
        );

        // Validate the ChildData in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertChildDataUpdatableFieldsEquals(returnedChildData, getPersistedChildData(returnedChildData));
    }

    @Test
    @Transactional
    void createChildDataWithExistingId() throws Exception {
        // Create the ChildData with an existing ID
        childData.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restChildDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(childData)))
            .andExpect(status().isBadRequest());

        // Validate the ChildData in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllChildData() throws Exception {
        // Initialize the database
        childDataRepository.saveAndFlush(childData);

        // Get all the childDataList
        restChildDataMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(childData.getId().intValue())))
            .andExpect(jsonPath("$.[*].childDataID").value(hasItem(DEFAULT_CHILD_DATA_ID)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].dob").value(hasItem(DEFAULT_DOB.toString())));
    }

    @Test
    @Transactional
    void getChildData() throws Exception {
        // Initialize the database
        childDataRepository.saveAndFlush(childData);

        // Get the childData
        restChildDataMockMvc
            .perform(get(ENTITY_API_URL_ID, childData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(childData.getId().intValue()))
            .andExpect(jsonPath("$.childDataID").value(DEFAULT_CHILD_DATA_ID))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.dob").value(DEFAULT_DOB.toString()));
    }

    @Test
    @Transactional
    void getNonExistingChildData() throws Exception {
        // Get the childData
        restChildDataMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingChildData() throws Exception {
        // Initialize the database
        childDataRepository.saveAndFlush(childData);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the childData
        ChildData updatedChildData = childDataRepository.findById(childData.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedChildData are not directly saved in db
        em.detach(updatedChildData);
        updatedChildData.childDataID(UPDATED_CHILD_DATA_ID).name(UPDATED_NAME).lastName(UPDATED_LAST_NAME).dob(UPDATED_DOB);

        restChildDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedChildData.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedChildData))
            )
            .andExpect(status().isOk());

        // Validate the ChildData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedChildDataToMatchAllProperties(updatedChildData);
    }

    @Test
    @Transactional
    void putNonExistingChildData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        childData.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChildDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, childData.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(childData))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChildData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchChildData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        childData.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChildDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(childData))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChildData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamChildData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        childData.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChildDataMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(childData)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChildData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateChildDataWithPatch() throws Exception {
        // Initialize the database
        childDataRepository.saveAndFlush(childData);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the childData using partial update
        ChildData partialUpdatedChildData = new ChildData();
        partialUpdatedChildData.setId(childData.getId());

        partialUpdatedChildData.childDataID(UPDATED_CHILD_DATA_ID);

        restChildDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChildData.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChildData))
            )
            .andExpect(status().isOk());

        // Validate the ChildData in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChildDataUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedChildData, childData),
            getPersistedChildData(childData)
        );
    }

    @Test
    @Transactional
    void fullUpdateChildDataWithPatch() throws Exception {
        // Initialize the database
        childDataRepository.saveAndFlush(childData);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the childData using partial update
        ChildData partialUpdatedChildData = new ChildData();
        partialUpdatedChildData.setId(childData.getId());

        partialUpdatedChildData.childDataID(UPDATED_CHILD_DATA_ID).name(UPDATED_NAME).lastName(UPDATED_LAST_NAME).dob(UPDATED_DOB);

        restChildDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChildData.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChildData))
            )
            .andExpect(status().isOk());

        // Validate the ChildData in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChildDataUpdatableFieldsEquals(partialUpdatedChildData, getPersistedChildData(partialUpdatedChildData));
    }

    @Test
    @Transactional
    void patchNonExistingChildData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        childData.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChildDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, childData.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(childData))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChildData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchChildData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        childData.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChildDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(childData))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChildData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamChildData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        childData.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChildDataMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(childData)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChildData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteChildData() throws Exception {
        // Initialize the database
        childDataRepository.saveAndFlush(childData);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the childData
        restChildDataMockMvc
            .perform(delete(ENTITY_API_URL_ID, childData.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return childDataRepository.count();
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

    protected ChildData getPersistedChildData(ChildData childData) {
        return childDataRepository.findById(childData.getId()).orElseThrow();
    }

    protected void assertPersistedChildDataToMatchAllProperties(ChildData expectedChildData) {
        assertChildDataAllPropertiesEquals(expectedChildData, getPersistedChildData(expectedChildData));
    }

    protected void assertPersistedChildDataToMatchUpdatableProperties(ChildData expectedChildData) {
        assertChildDataAllUpdatablePropertiesEquals(expectedChildData, getPersistedChildData(expectedChildData));
    }
}
