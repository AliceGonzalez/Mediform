package mediformapp.domain;

import static mediformapp.domain.TemplateFormTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import mediformapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TemplateFormTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TemplateForm.class);
        TemplateForm templateForm1 = getTemplateFormSample1();
        TemplateForm templateForm2 = new TemplateForm();
        assertThat(templateForm1).isNotEqualTo(templateForm2);

        templateForm2.setId(templateForm1.getId());
        assertThat(templateForm1).isEqualTo(templateForm2);

        templateForm2 = getTemplateFormSample2();
        assertThat(templateForm1).isNotEqualTo(templateForm2);
    }
}
