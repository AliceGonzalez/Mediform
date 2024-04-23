package mediformapp.domain;

import static mediformapp.domain.ChildDataTestSamples.*;
import static mediformapp.domain.ChildTestSamples.*;
import static mediformapp.domain.FormStatusTestSamples.*;
import static mediformapp.domain.ParentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import mediformapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChildTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Child.class);
        Child child1 = getChildSample1();
        Child child2 = new Child();
        assertThat(child1).isNotEqualTo(child2);

        child2.setId(child1.getId());
        assertThat(child1).isEqualTo(child2);

        child2 = getChildSample2();
        assertThat(child1).isNotEqualTo(child2);
    }

    @Test
    void formStatusTest() throws Exception {
        Child child = getChildRandomSampleGenerator();
        FormStatus formStatusBack = getFormStatusRandomSampleGenerator();

        child.addFormStatus(formStatusBack);
        assertThat(child.getFormStatuses()).containsOnly(formStatusBack);
        assertThat(formStatusBack.getChild()).isEqualTo(child);

        child.removeFormStatus(formStatusBack);
        assertThat(child.getFormStatuses()).doesNotContain(formStatusBack);
        assertThat(formStatusBack.getChild()).isNull();

        child.formStatuses(new HashSet<>(Set.of(formStatusBack)));
        assertThat(child.getFormStatuses()).containsOnly(formStatusBack);
        assertThat(formStatusBack.getChild()).isEqualTo(child);

        child.setFormStatuses(new HashSet<>());
        assertThat(child.getFormStatuses()).doesNotContain(formStatusBack);
        assertThat(formStatusBack.getChild()).isNull();
    }

    @Test
    void parentTest() throws Exception {
        Child child = getChildRandomSampleGenerator();
        Parent parentBack = getParentRandomSampleGenerator();

        child.setParent(parentBack);
        assertThat(child.getParent()).isEqualTo(parentBack);

        child.parent(null);
        assertThat(child.getParent()).isNull();
    }

    @Test
    void childDataTest() throws Exception {
        Child child = getChildRandomSampleGenerator();
        ChildData childDataBack = getChildDataRandomSampleGenerator();

        child.setChildData(childDataBack);
        assertThat(child.getChildData()).isEqualTo(childDataBack);
        assertThat(childDataBack.getChild()).isEqualTo(child);

        child.childData(null);
        assertThat(child.getChildData()).isNull();
        assertThat(childDataBack.getChild()).isNull();
    }
}
