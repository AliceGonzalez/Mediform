package mediformapp.domain;

import static mediformapp.domain.ChildTestSamples.*;
import static mediformapp.domain.LoginTestSamples.*;
import static mediformapp.domain.ParentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import mediformapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ParentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Parent.class);
        Parent parent1 = getParentSample1();
        Parent parent2 = new Parent();
        assertThat(parent1).isNotEqualTo(parent2);

        parent2.setId(parent1.getId());
        assertThat(parent1).isEqualTo(parent2);

        parent2 = getParentSample2();
        assertThat(parent1).isNotEqualTo(parent2);
    }

    @Test
    void childTest() throws Exception {
        Parent parent = getParentRandomSampleGenerator();
        Child childBack = getChildRandomSampleGenerator();

        parent.addChild(childBack);
        assertThat(parent.getChildren()).containsOnly(childBack);
        assertThat(childBack.getParent()).isEqualTo(parent);

        parent.removeChild(childBack);
        assertThat(parent.getChildren()).doesNotContain(childBack);
        assertThat(childBack.getParent()).isNull();

        parent.children(new HashSet<>(Set.of(childBack)));
        assertThat(parent.getChildren()).containsOnly(childBack);
        assertThat(childBack.getParent()).isEqualTo(parent);

        parent.setChildren(new HashSet<>());
        assertThat(parent.getChildren()).doesNotContain(childBack);
        assertThat(childBack.getParent()).isNull();
    }

    @Test
    void loginTest() throws Exception {
        Parent parent = getParentRandomSampleGenerator();
        Login loginBack = getLoginRandomSampleGenerator();

        parent.setLogin(loginBack);
        assertThat(parent.getLogin()).isEqualTo(loginBack);
        assertThat(loginBack.getParentID()).isEqualTo(parent);

        parent.login(null);
        assertThat(parent.getLogin()).isNull();
        assertThat(loginBack.getParentID()).isNull();
    }
}
