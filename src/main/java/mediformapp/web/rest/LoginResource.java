package mediformapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import mediformapp.domain.Login;
import mediformapp.repository.LoginRepository;
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
 * REST controller for managing {@link mediformapp.domain.Login}.
 */
@RestController
@RequestMapping("/api/logins")
@Transactional
public class LoginResource {

    private final Logger log = LoggerFactory.getLogger(LoginResource.class);

    private static final String ENTITY_NAME = "login";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LoginRepository loginRepository;

    public LoginResource(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    /**
     * {@code POST  /logins} : Create a new login.
     *
     * @param login the login to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new login, or with status {@code 400 (Bad Request)} if the login has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Login> createLogin(@RequestBody Login login) throws URISyntaxException {
        log.debug("REST request to save Login : {}", login);
        if (login.getId() != null) {
            throw new BadRequestAlertException("A new login cannot already have an ID", ENTITY_NAME, "idexists");
        }
        login = loginRepository.save(login);
        return ResponseEntity.created(new URI("/api/logins/" + login.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, login.getId().toString()))
            .body(login);
    }

    /**
     * {@code PUT  /logins/:id} : Updates an existing login.
     *
     * @param id the id of the login to save.
     * @param login the login to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated login,
     * or with status {@code 400 (Bad Request)} if the login is not valid,
     * or with status {@code 500 (Internal Server Error)} if the login couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Login> updateLogin(@PathVariable(value = "id", required = false) final Long id, @RequestBody Login login)
        throws URISyntaxException {
        log.debug("REST request to update Login : {}, {}", id, login);
        if (login.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, login.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!loginRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        login = loginRepository.save(login);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, login.getId().toString()))
            .body(login);
    }

    /**
     * {@code PATCH  /logins/:id} : Partial updates given fields of an existing login, field will ignore if it is null
     *
     * @param id the id of the login to save.
     * @param login the login to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated login,
     * or with status {@code 400 (Bad Request)} if the login is not valid,
     * or with status {@code 404 (Not Found)} if the login is not found,
     * or with status {@code 500 (Internal Server Error)} if the login couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Login> partialUpdateLogin(@PathVariable(value = "id", required = false) final Long id, @RequestBody Login login)
        throws URISyntaxException {
        log.debug("REST request to partial update Login partially : {}, {}", id, login);
        if (login.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, login.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!loginRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Login> result = loginRepository
            .findById(login.getId())
            .map(existingLogin -> {
                if (login.getUsername() != null) {
                    existingLogin.setUsername(login.getUsername());
                }
                if (login.getPassword() != null) {
                    existingLogin.setPassword(login.getPassword());
                }

                return existingLogin;
            })
            .map(loginRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, login.getId().toString())
        );
    }

    /**
     * {@code GET  /logins} : get all the logins.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of logins in body.
     */
    @GetMapping("")
    public List<Login> getAllLogins() {
        log.debug("REST request to get all Logins");
        return loginRepository.findAll();
    }

    /**
     * {@code GET  /logins/:id} : get the "id" login.
     *
     * @param id the id of the login to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the login, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Login> getLogin(@PathVariable("id") Long id) {
        log.debug("REST request to get Login : {}", id);
        Optional<Login> login = loginRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(login);
    }

    /**
     * {@code DELETE  /logins/:id} : delete the "id" login.
     *
     * @param id the id of the login to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLogin(@PathVariable("id") Long id) {
        log.debug("REST request to delete Login : {}", id);
        loginRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
