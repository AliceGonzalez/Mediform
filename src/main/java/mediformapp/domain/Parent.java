package mediformapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Parent.
 */
@Entity
@Table(name = "parent")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Parent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "parent_id")
    private Integer parentID;

    @Column(name = "name")
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "formStatuses", "parent", "childData" }, allowSetters = true)
    private Set<Child> children = new HashSet<>();

    @JsonIgnoreProperties(value = { "parentID" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "parentID")
    private Login login;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Parent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getParentID() {
        return this.parentID;
    }

    public Parent parentID(Integer parentID) {
        this.setParentID(parentID);
        return this;
    }

    public void setParentID(Integer parentID) {
        this.parentID = parentID;
    }

    public String getName() {
        return this.name;
    }

    public Parent name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Parent lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Child> getChildren() {
        return this.children;
    }

    public void setChildren(Set<Child> children) {
        if (this.children != null) {
            this.children.forEach(i -> i.setParent(null));
        }
        if (children != null) {
            children.forEach(i -> i.setParent(this));
        }
        this.children = children;
    }

    public Parent children(Set<Child> children) {
        this.setChildren(children);
        return this;
    }

    public Parent addChild(Child child) {
        this.children.add(child);
        child.setParent(this);
        return this;
    }

    public Parent removeChild(Child child) {
        this.children.remove(child);
        child.setParent(null);
        return this;
    }

    public Login getLogin() {
        return this.login;
    }

    public void setLogin(Login login) {
        if (this.login != null) {
            this.login.setParentID(null);
        }
        if (login != null) {
            login.setParentID(this);
        }
        this.login = login;
    }

    public Parent login(Login login) {
        this.setLogin(login);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Parent)) {
            return false;
        }
        return getId() != null && getId().equals(((Parent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Parent{" +
            "id=" + getId() +
            ", parentID=" + getParentID() +
            ", name='" + getName() + "'" +
            ", lastName='" + getLastName() + "'" +
            "}";
    }
}
