package mediformapp.web.rest;

import static mediformapp.domain.ChildAsserts.*;
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
import mediformapp.domain.Child;
import mediformapp.repository.ChildRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ChildResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ChildResourceIT {

    private static final Integer DEFAULT_CHILD_ID = 1;
    private static final Integer UPDATED_CHILD_ID = 2;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DOB = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DOB = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/children";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChildMockMvc;

    private Child child;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Child createEntity(EntityManager em) {
        Child child = new Child().childID(DEFAULT_CHILD_ID).name(DEFAULT_NAME).lastName(DEFAULT_LAST_NAME).dob(DEFAULT_DOB);
        return child;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Child createUpdatedEntity(EntityManager em) {
        Child child = new Child().childID(UPDATED_CHILD_ID).name(UPDATED_NAME).lastName(UPDATED_LAST_NAME).dob(UPDATED_DOB);
        return child;
    }

    @BeforeEach
    public void initTest() {
        child = createEntity(em);
    }

    @Test
    @Transactional
    void createChild() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Child
        var returnedChild = om.readValue(
            restChildMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(child)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Child.class
        );

        // Validate the Child in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertChildUpdatableFieldsEquals(returnedChild, getPersistedChild(returnedChild));
    }

    @Test
    @Transactional
    void createChildWithExistingId() throws Exception {
        // Create the Child with an existing ID
        child.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restChildMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(child)))
            .andExpect(status().isBadRequest());

        // Validate the Child in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllChildren() throws Exception {
        // Initialize the database
        childRepository.saveAndFlush(child);

        // Get all the childList
        restChildMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(child.getId().intValue())))
            .andExpect(jsonPath("$.[*].childID").value(hasItem(DEFAULT_CHILD_ID)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].dob").value(hasItem(DEFAULT_DOB.toString())));
    }

    @Test
    @Transactional
    void getChild() throws Exception {
        // Initialize the database
        childRepository.saveAndFlush(child);

        // Get the child
        restChildMockMvc
            .perform(get(ENTITY_API_URL_ID, child.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(child.getId().intValue()))
            .andExpect(jsonPath("$.childID").value(DEFAULT_CHILD_ID))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.dob").value(DEFAULT_DOB.toString()));
    }

    @Test
    @Transactional
    void getNonExistingChild() throws Exception {
        // Get the child
        restChildMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingChild() throws Exception {
        // Initialize the database
        childRepository.saveAndFlush(child);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the child
        Child updatedChild = childRepository.findById(child.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedChild are not directly saved in db
        em.detach(updatedChild);
        updatedChild.childID(UPDATED_CHILD_ID).name(UPDATED_NAME).lastName(UPDATED_LAST_NAME).dob(UPDATED_DOB);

        restChildMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedChild.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedChild))
            )
            .andExpect(status().isOk());

        // Validate the Child in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedChildToMatchAllProperties(updatedChild);
    }

    @Test
    @Transactional
    void putNonExistingChild() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        child.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChildMockMvc
            .perform(put(ENTITY_API_URL_ID, child.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(child)))
            .andExpect(status().isBadRequest());

        // Validate the Child in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchChild() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        child.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChildMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(child))
            )
            .andExpect(status().isBadRequest());

        // Validate the Child in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamChild() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        child.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChildMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(child)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Child in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateChildWithPatch() throws Exception {
        // Initialize the database
        childRepository.saveAndFlush(child);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the child using partial update
        Child partialUpdatedChild = new Child();
        partialUpdatedChild.setId(child.getId());

        partialUpdatedChild.name(UPDATED_NAME).lastName(UPDATED_LAST_NAME);

        restChildMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChild.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChild))
            )
            .andExpect(status().isOk());

        // Validate the Child in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChildUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedChild, child), getPersistedChild(child));
    }

    @Test
    @Transactional
    void fullUpdateChildWithPatch() throws Exception {
        // Initialize the database
        childRepository.saveAndFlush(child);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the child using partial update
        Child partialUpdatedChild = new Child();
        partialUpdatedChild.setId(child.getId());

        partialUpdatedChild.childID(UPDATED_CHILD_ID).name(UPDATED_NAME).lastName(UPDATED_LAST_NAME).dob(UPDATED_DOB);

        restChildMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChild.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChild))
            )
            .andExpect(status().isOk());

        // Validate the Child in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChildUpdatableFieldsEquals(partialUpdatedChild, getPersistedChild(partialUpdatedChild));
    }

    @Test
    @Transactional
    void patchNonExistingChild() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        child.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChildMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, child.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(child))
            )
            .andExpect(status().isBadRequest());

        // Validate the Child in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchChild() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        child.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChildMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(child))
            )
            .andExpect(status().isBadRequest());

        // Validate the Child in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamChild() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        child.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChildMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(child)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Child in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteChild() throws Exception {
        // Initialize the database
        childRepository.saveAndFlush(child);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the child
        restChildMockMvc
            .perform(delete(ENTITY_API_URL_ID, child.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return childRepository.count();
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

    protected Child getPersistedChild(Child child) {
        return childRepository.findById(child.getId()).orElseThrow();
    }

    protected void assertPersistedChildToMatchAllProperties(Child expectedChild) {
        assertChildAllPropertiesEquals(expectedChild, getPersistedChild(expectedChild));
    }

    protected void assertPersistedChildToMatchUpdatableProperties(Child expectedChild) {
        assertChildAllUpdatablePropertiesEquals(expectedChild, getPersistedChild(expectedChild));
    }
}
