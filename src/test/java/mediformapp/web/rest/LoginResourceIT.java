package mediformapp.web.rest;

import static mediformapp.domain.LoginAsserts.*;
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
import mediformapp.domain.Login;
import mediformapp.repository.LoginRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link LoginResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LoginResourceIT {

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/logins";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLoginMockMvc;

    private Login login;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Login createEntity(EntityManager em) {
        Login login = new Login().username(DEFAULT_USERNAME).password(DEFAULT_PASSWORD);
        return login;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Login createUpdatedEntity(EntityManager em) {
        Login login = new Login().username(UPDATED_USERNAME).password(UPDATED_PASSWORD);
        return login;
    }

    @BeforeEach
    public void initTest() {
        login = createEntity(em);
    }

    @Test
    @Transactional
    void createLogin() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Login
        var returnedLogin = om.readValue(
            restLoginMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(login)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Login.class
        );

        // Validate the Login in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertLoginUpdatableFieldsEquals(returnedLogin, getPersistedLogin(returnedLogin));
    }

    @Test
    @Transactional
    void createLoginWithExistingId() throws Exception {
        // Create the Login with an existing ID
        login.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLoginMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(login)))
            .andExpect(status().isBadRequest());

        // Validate the Login in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllLogins() throws Exception {
        // Initialize the database
        loginRepository.saveAndFlush(login);

        // Get all the loginList
        restLoginMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(login.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)));
    }

    @Test
    @Transactional
    void getLogin() throws Exception {
        // Initialize the database
        loginRepository.saveAndFlush(login);

        // Get the login
        restLoginMockMvc
            .perform(get(ENTITY_API_URL_ID, login.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(login.getId().intValue()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD));
    }

    @Test
    @Transactional
    void getNonExistingLogin() throws Exception {
        // Get the login
        restLoginMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLogin() throws Exception {
        // Initialize the database
        loginRepository.saveAndFlush(login);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the login
        Login updatedLogin = loginRepository.findById(login.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLogin are not directly saved in db
        em.detach(updatedLogin);
        updatedLogin.username(UPDATED_USERNAME).password(UPDATED_PASSWORD);

        restLoginMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLogin.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedLogin))
            )
            .andExpect(status().isOk());

        // Validate the Login in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLoginToMatchAllProperties(updatedLogin);
    }

    @Test
    @Transactional
    void putNonExistingLogin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        login.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLoginMockMvc
            .perform(put(ENTITY_API_URL_ID, login.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(login)))
            .andExpect(status().isBadRequest());

        // Validate the Login in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLogin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        login.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoginMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(login))
            )
            .andExpect(status().isBadRequest());

        // Validate the Login in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLogin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        login.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoginMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(login)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Login in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLoginWithPatch() throws Exception {
        // Initialize the database
        loginRepository.saveAndFlush(login);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the login using partial update
        Login partialUpdatedLogin = new Login();
        partialUpdatedLogin.setId(login.getId());

        partialUpdatedLogin.password(UPDATED_PASSWORD);

        restLoginMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLogin.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLogin))
            )
            .andExpect(status().isOk());

        // Validate the Login in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLoginUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedLogin, login), getPersistedLogin(login));
    }

    @Test
    @Transactional
    void fullUpdateLoginWithPatch() throws Exception {
        // Initialize the database
        loginRepository.saveAndFlush(login);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the login using partial update
        Login partialUpdatedLogin = new Login();
        partialUpdatedLogin.setId(login.getId());

        partialUpdatedLogin.username(UPDATED_USERNAME).password(UPDATED_PASSWORD);

        restLoginMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLogin.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLogin))
            )
            .andExpect(status().isOk());

        // Validate the Login in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLoginUpdatableFieldsEquals(partialUpdatedLogin, getPersistedLogin(partialUpdatedLogin));
    }

    @Test
    @Transactional
    void patchNonExistingLogin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        login.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLoginMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, login.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(login))
            )
            .andExpect(status().isBadRequest());

        // Validate the Login in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLogin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        login.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoginMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(login))
            )
            .andExpect(status().isBadRequest());

        // Validate the Login in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLogin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        login.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoginMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(login)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Login in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLogin() throws Exception {
        // Initialize the database
        loginRepository.saveAndFlush(login);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the login
        restLoginMockMvc
            .perform(delete(ENTITY_API_URL_ID, login.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return loginRepository.count();
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

    protected Login getPersistedLogin(Login login) {
        return loginRepository.findById(login.getId()).orElseThrow();
    }

    protected void assertPersistedLoginToMatchAllProperties(Login expectedLogin) {
        assertLoginAllPropertiesEquals(expectedLogin, getPersistedLogin(expectedLogin));
    }

    protected void assertPersistedLoginToMatchUpdatableProperties(Login expectedLogin) {
        assertLoginAllUpdatablePropertiesEquals(expectedLogin, getPersistedLogin(expectedLogin));
    }
}
