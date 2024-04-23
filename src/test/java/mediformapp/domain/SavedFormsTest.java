package mediformapp.domain;

import static mediformapp.domain.ChildTestSamples.*;
import static mediformapp.domain.SavedFormsTestSamples.*;
import static mediformapp.domain.TemplateFormTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import mediformapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SavedFormsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SavedForms.class);
        SavedForms savedForms1 = getSavedFormsSample1();
        SavedForms savedForms2 = new SavedForms();
        assertThat(savedForms1).isNotEqualTo(savedForms2);

        savedForms2.setId(savedForms1.getId());
        assertThat(savedForms1).isEqualTo(savedForms2);

        savedForms2 = getSavedFormsSample2();
        assertThat(savedForms1).isNotEqualTo(savedForms2);
    }

    @Test
    void childTest() throws Exception {
        SavedForms savedForms = getSavedFormsRandomSampleGenerator();
        Child childBack = getChildRandomSampleGenerator();

        savedForms.setChild(childBack);
        assertThat(savedForms.getChild()).isEqualTo(childBack);

        savedForms.child(null);
        assertThat(savedForms.getChild()).isNull();
    }

    @Test
    void templateFormTest() throws Exception {
        SavedForms savedForms = getSavedFormsRandomSampleGenerator();
        TemplateForm templateFormBack = getTemplateFormRandomSampleGenerator();

        savedForms.setTemplateForm(templateFormBack);
        assertThat(savedForms.getTemplateForm()).isEqualTo(templateFormBack);

        savedForms.templateForm(null);
        assertThat(savedForms.getTemplateForm()).isNull();
    }
}
