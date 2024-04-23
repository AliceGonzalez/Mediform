package mediformapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A FormStatus.
 */
@Entity
@Table(name = "form_status")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FormStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "form_status_id")
    private Integer formStatusID;

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    private TemplateForm templateForm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "formStatuses", "parent", "childData" }, allowSetters = true)
    private Child child;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FormStatus id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFormStatusID() {
        return this.formStatusID;
    }

    public FormStatus formStatusID(Integer formStatusID) {
        this.setFormStatusID(formStatusID);
        return this;
    }

    public void setFormStatusID(Integer formStatusID) {
        this.formStatusID = formStatusID;
    }

    public String getStatus() {
        return this.status;
    }

    public FormStatus status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TemplateForm getTemplateForm() {
        return this.templateForm;
    }

    public void setTemplateForm(TemplateForm templateForm) {
        this.templateForm = templateForm;
    }

    public FormStatus templateForm(TemplateForm templateForm) {
        this.setTemplateForm(templateForm);
        return this;
    }

    public Child getChild() {
        return this.child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public FormStatus child(Child child) {
        this.setChild(child);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FormStatus)) {
            return false;
        }
        return getId() != null && getId().equals(((FormStatus) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FormStatus{" +
            "id=" + getId() +
            ", formStatusID=" + getFormStatusID() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
