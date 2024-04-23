package mediformapp.domain;

import static mediformapp.domain.ChildTestSamples.*;
import static mediformapp.domain.FormStatusTestSamples.*;
import static mediformapp.domain.TemplateFormTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import mediformapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FormStatusTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FormStatus.class);
        FormStatus formStatus1 = getFormStatusSample1();
        FormStatus formStatus2 = new FormStatus();
        assertThat(formStatus1).isNotEqualTo(formStatus2);

        formStatus2.setId(formStatus1.getId());
        assertThat(formStatus1).isEqualTo(formStatus2);

        formStatus2 = getFormStatusSample2();
        assertThat(formStatus1).isNotEqualTo(formStatus2);
    }

    @Test
    void templateFormTest() throws Exception {
        FormStatus formStatus = getFormStatusRandomSampleGenerator();
        TemplateForm templateFormBack = getTemplateFormRandomSampleGenerator();

        formStatus.setTemplateForm(templateFormBack);
        assertThat(formStatus.getTemplateForm()).isEqualTo(templateFormBack);

        formStatus.templateForm(null);
        assertThat(formStatus.getTemplateForm()).isNull();
    }

    @Test
    void childTest() throws Exception {
        FormStatus formStatus = getFormStatusRandomSampleGenerator();
        Child childBack = getChildRandomSampleGenerator();

        formStatus.setChild(childBack);
        assertThat(formStatus.getChild()).isEqualTo(childBack);

        formStatus.child(null);
        assertThat(formStatus.getChild()).isNull();
    }
}
