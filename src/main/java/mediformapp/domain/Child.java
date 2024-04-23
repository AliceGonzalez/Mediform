package mediformapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Child.
 */
@Entity
@Table(name = "child")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Child implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "child_id")
    private Integer childID;

    @Column(name = "name")
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "dob")
    private LocalDate dob;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "child")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "templateForm", "child" }, allowSetters = true)
    private Set<FormStatus> formStatuses = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "children", "login" }, allowSetters = true)
    private Parent parent;

    @JsonIgnoreProperties(value = { "child" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "child")
    private ChildData childData;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Child id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getChildID() {
        return this.childID;
    }

    public Child childID(Integer childID) {
        this.setChildID(childID);
        return this;
    }

    public void setChildID(Integer childID) {
        this.childID = childID;
    }

    public String getName() {
        return this.name;
    }

    public Child name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Child lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDob() {
        return this.dob;
    }

    public Child dob(LocalDate dob) {
        this.setDob(dob);
        return this;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Set<FormStatus> getFormStatuses() {
        return this.formStatuses;
    }

    public void setFormStatuses(Set<FormStatus> formStatuses) {
        if (this.formStatuses != null) {
            this.formStatuses.forEach(i -> i.setChild(null));
        }
        if (formStatuses != null) {
            formStatuses.forEach(i -> i.setChild(this));
        }
        this.formStatuses = formStatuses;
    }

    public Child formStatuses(Set<FormStatus> formStatuses) {
        this.setFormStatuses(formStatuses);
        return this;
    }

    public Child addFormStatus(FormStatus formStatus) {
        this.formStatuses.add(formStatus);
        formStatus.setChild(this);
        return this;
    }

    public Child removeFormStatus(FormStatus formStatus) {
        this.formStatuses.remove(formStatus);
        formStatus.setChild(null);
        return this;
    }

    public Parent getParent() {
        return this.parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public Child parent(Parent parent) {
        this.setParent(parent);
        return this;
    }

    public ChildData getChildData() {
        return this.childData;
    }

    public void setChildData(ChildData childData) {
        if (this.childData != null) {
            this.childData.setChild(null);
        }
        if (childData != null) {
            childData.setChild(this);
        }
        this.childData = childData;
    }

    public Child childData(ChildData childData) {
        this.setChildData(childData);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Child)) {
            return false;
        }
        return getId() != null && getId().equals(((Child) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Child{" +
            "id=" + getId() +
            ", childID=" + getChildID() +
            ", name='" + getName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", dob='" + getDob() + "'" +
            "}";
    }
}
