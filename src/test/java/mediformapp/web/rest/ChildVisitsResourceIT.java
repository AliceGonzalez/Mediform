package mediformapp.web.rest;

import static mediformapp.domain.ChildVisitsAsserts.*;
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
import mediformapp.domain.ChildVisits;
import mediformapp.repository.ChildVisitsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ChildVisitsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ChildVisitsResourceIT {

    private static final Integer DEFAULT_VISIT_ID = 1;
    private static final Integer UPDATED_VISIT_ID = 2;

    private static final String DEFAULT_VISIT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_VISIT_TYPE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_VISIT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_VISIT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/child-visits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ChildVisitsRepository childVisitsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChildVisitsMockMvc;

    private ChildVisits childVisits;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChildVisits createEntity(EntityManager em) {
        ChildVisits childVisits = new ChildVisits().visitID(DEFAULT_VISIT_ID).visitType(DEFAULT_VISIT_TYPE).visitDate(DEFAULT_VISIT_DATE);
        return childVisits;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChildVisits createUpdatedEntity(EntityManager em) {
        ChildVisits childVisits = new ChildVisits().visitID(UPDATED_VISIT_ID).visitType(UPDATED_VISIT_TYPE).visitDate(UPDATED_VISIT_DATE);
        return childVisits;
    }

    @BeforeEach
    public void initTest() {
        childVisits = createEntity(em);
    }

    @Test
    @Transactional
    void createChildVisits() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ChildVisits
        var returnedChildVisits = om.readValue(
            restChildVisitsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(childVisits)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ChildVisits.class
        );

        // Validate the ChildVisits in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertChildVisitsUpdatableFieldsEquals(returnedChildVisits, getPersistedChildVisits(returnedChildVisits));
    }

    @Test
    @Transactional
    void createChildVisitsWithExistingId() throws Exception {
        // Create the ChildVisits with an existing ID
        childVisits.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restChildVisitsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(childVisits)))
            .andExpect(status().isBadRequest());

        // Validate the ChildVisits in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllChildVisits() throws Exception {
        // Initialize the database
        childVisitsRepository.saveAndFlush(childVisits);

        // Get all the childVisitsList
        restChildVisitsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(childVisits.getId().intValue())))
            .andExpect(jsonPath("$.[*].visitID").value(hasItem(DEFAULT_VISIT_ID)))
            .andExpect(jsonPath("$.[*].visitType").value(hasItem(DEFAULT_VISIT_TYPE)))
            .andExpect(jsonPath("$.[*].visitDate").value(hasItem(DEFAULT_VISIT_DATE.toString())));
    }

    @Test
    @Transactional
    void getChildVisits() throws Exception {
        // Initialize the database
        childVisitsRepository.saveAndFlush(childVisits);

        // Get the childVisits
        restChildVisitsMockMvc
            .perform(get(ENTITY_API_URL_ID, childVisits.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(childVisits.getId().intValue()))
            .andExpect(jsonPath("$.visitID").value(DEFAULT_VISIT_ID))
            .andExpect(jsonPath("$.visitType").value(DEFAULT_VISIT_TYPE))
            .andExpect(jsonPath("$.visitDate").value(DEFAULT_VISIT_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingChildVisits() throws Exception {
        // Get the childVisits
        restChildVisitsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingChildVisits() throws Exception {
        // Initialize the database
        childVisitsRepository.saveAndFlush(childVisits);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the childVisits
        ChildVisits updatedChildVisits = childVisitsRepository.findById(childVisits.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedChildVisits are not directly saved in db
        em.detach(updatedChildVisits);
        updatedChildVisits.visitID(UPDATED_VISIT_ID).visitType(UPDATED_VISIT_TYPE).visitDate(UPDATED_VISIT_DATE);

        restChildVisitsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedChildVisits.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedChildVisits))
            )
            .andExpect(status().isOk());

        // Validate the ChildVisits in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedChildVisitsToMatchAllProperties(updatedChildVisits);
    }

    @Test
    @Transactional
    void putNonExistingChildVisits() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        childVisits.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChildVisitsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, childVisits.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(childVisits))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChildVisits in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchChildVisits() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        childVisits.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChildVisitsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(childVisits))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChildVisits in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamChildVisits() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        childVisits.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChildVisitsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(childVisits)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChildVisits in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateChildVisitsWithPatch() throws Exception {
        // Initialize the database
        childVisitsRepository.saveAndFlush(childVisits);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the childVisits using partial update
        ChildVisits partialUpdatedChildVisits = new ChildVisits();
        partialUpdatedChildVisits.setId(childVisits.getId());

        partialUpdatedChildVisits.visitID(UPDATED_VISIT_ID).visitDate(UPDATED_VISIT_DATE);

        restChildVisitsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChildVisits.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChildVisits))
            )
            .andExpect(status().isOk());

        // Validate the ChildVisits in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChildVisitsUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedChildVisits, childVisits),
            getPersistedChildVisits(childVisits)
        );
    }

    @Test
    @Transactional
    void fullUpdateChildVisitsWithPatch() throws Exception {
        // Initialize the database
        childVisitsRepository.saveAndFlush(childVisits);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the childVisits using partial update
        ChildVisits partialUpdatedChildVisits = new ChildVisits();
        partialUpdatedChildVisits.setId(childVisits.getId());

        partialUpdatedChildVisits.visitID(UPDATED_VISIT_ID).visitType(UPDATED_VISIT_TYPE).visitDate(UPDATED_VISIT_DATE);

        restChildVisitsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChildVisits.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChildVisits))
            )
            .andExpect(status().isOk());

        // Validate the ChildVisits in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChildVisitsUpdatableFieldsEquals(partialUpdatedChildVisits, getPersistedChildVisits(partialUpdatedChildVisits));
    }

    @Test
    @Transactional
    void patchNonExistingChildVisits() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        childVisits.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChildVisitsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, childVisits.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(childVisits))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChildVisits in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchChildVisits() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        childVisits.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChildVisitsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(childVisits))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChildVisits in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamChildVisits() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        childVisits.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChildVisitsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(childVisits)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChildVisits in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteChildVisits() throws Exception {
        // Initialize the database
        childVisitsRepository.saveAndFlush(childVisits);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the childVisits
        restChildVisitsMockMvc
            .perform(delete(ENTITY_API_URL_ID, childVisits.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return childVisitsRepository.count();
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

    protected ChildVisits getPersistedChildVisits(ChildVisits childVisits) {
        return childVisitsRepository.findById(childVisits.getId()).orElseThrow();
    }

    protected void assertPersistedChildVisitsToMatchAllProperties(ChildVisits expectedChildVisits) {
        assertChildVisitsAllPropertiesEquals(expectedChildVisits, getPersistedChildVisits(expectedChildVisits));
    }

    protected void assertPersistedChildVisitsToMatchUpdatableProperties(ChildVisits expectedChildVisits) {
        assertChildVisitsAllUpdatablePropertiesEquals(expectedChildVisits, getPersistedChildVisits(expectedChildVisits));
    }
}
