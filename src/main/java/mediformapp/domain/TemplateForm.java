package mediformapp.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TemplateForm.
 */
@Entity
@Table(name = "template_form")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TemplateForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "template_form_id")
    private Integer templateFormID;

    @Column(name = "form_type")
    private String formType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TemplateForm id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTemplateFormID() {
        return this.templateFormID;
    }

    public TemplateForm templateFormID(Integer templateFormID) {
        this.setTemplateFormID(templateFormID);
        return this;
    }

    public void setTemplateFormID(Integer templateFormID) {
        this.templateFormID = templateFormID;
    }

    public String getFormType() {
        return this.formType;
    }

    public TemplateForm formType(String formType) {
        this.setFormType(formType);
        return this;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TemplateForm)) {
            return false;
        }
        return getId() != null && getId().equals(((TemplateForm) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TemplateForm{" +
            "id=" + getId() +
            ", templateFormID=" + getTemplateFormID() +
            ", formType='" + getFormType() + "'" +
            "}";
    }
}
