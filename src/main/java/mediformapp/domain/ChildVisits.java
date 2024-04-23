package mediformapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ChildVisits.
 */
@Entity
@Table(name = "child_visits")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChildVisits implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "visit_id")
    private Integer visitID;

    @Column(name = "visit_type")
    private String visitType;

    @Column(name = "visit_date")
    private LocalDate visitDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "formStatuses", "parent", "childData" }, allowSetters = true)
    private Child child;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ChildVisits id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVisitID() {
        return this.visitID;
    }

    public ChildVisits visitID(Integer visitID) {
        this.setVisitID(visitID);
        return this;
    }

    public void setVisitID(Integer visitID) {
        this.visitID = visitID;
    }

    public String getVisitType() {
        return this.visitType;
    }

    public ChildVisits visitType(String visitType) {
        this.setVisitType(visitType);
        return this;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public LocalDate getVisitDate() {
        return this.visitDate;
    }

    public ChildVisits visitDate(LocalDate visitDate) {
        this.setVisitDate(visitDate);
        return this;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    public Child getChild() {
        return this.child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public ChildVisits child(Child child) {
        this.setChild(child);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChildVisits)) {
            return false;
        }
        return getId() != null && getId().equals(((ChildVisits) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChildVisits{" +
            "id=" + getId() +
            ", visitID=" + getVisitID() +
            ", visitType='" + getVisitType() + "'" +
            ", visitDate='" + getVisitDate() + "'" +
            "}";
    }
}
