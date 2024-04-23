package mediformapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SavedForms.
 */
@Entity
@Table(name = "saved_forms")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SavedForms implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "saved_form_id")
    private Integer savedFormID;

    @Column(name = "form_id")
    private Integer formID;

    @Column(name = "form_type")
    private String formType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "formStatuses", "parent", "childData" }, allowSetters = true)
    private Child child;

    @ManyToOne(fetch = FetchType.LAZY)
    private TemplateForm templateForm;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SavedForms id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSavedFormID() {
        return this.savedFormID;
    }

    public SavedForms savedFormID(Integer savedFormID) {
        this.setSavedFormID(savedFormID);
        return this;
    }

    public void setSavedFormID(Integer savedFormID) {
        this.savedFormID = savedFormID;
    }

    public Integer getFormID() {
        return this.formID;
    }

    public SavedForms formID(Integer formID) {
        this.setFormID(formID);
        return this;
    }

    public void setFormID(Integer formID) {
        this.formID = formID;
    }

    public String getFormType() {
        return this.formType;
    }

    public SavedForms formType(String formType) {
        this.setFormType(formType);
        return this;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public Child getChild() {
        return this.child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public SavedForms child(Child child) {
        this.setChild(child);
        return this;
    }

    public TemplateForm getTemplateForm() {
        return this.templateForm;
    }

    public void setTemplateForm(TemplateForm templateForm) {
        this.templateForm = templateForm;
    }

    public SavedForms templateForm(TemplateForm templateForm) {
        this.setTemplateForm(templateForm);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SavedForms)) {
            return false;
        }
        return getId() != null && getId().equals(((SavedForms) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SavedForms{" +
            "id=" + getId() +
            ", savedFormID=" + getSavedFormID() +
            ", formID=" + getFormID() +
            ", formType='" + getFormType() + "'" +
            "}";
    }
}
