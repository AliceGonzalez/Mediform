package mediformapp.domain;

import static mediformapp.domain.LoginTestSamples.*;
import static mediformapp.domain.ParentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import mediformapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LoginTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Login.class);
        Login login1 = getLoginSample1();
        Login login2 = new Login();
        assertThat(login1).isNotEqualTo(login2);

        login2.setId(login1.getId());
        assertThat(login1).isEqualTo(login2);

        login2 = getLoginSample2();
        assertThat(login1).isNotEqualTo(login2);
    }

    @Test
    void parentIDTest() throws Exception {
        Login login = getLoginRandomSampleGenerator();
        Parent parentBack = getParentRandomSampleGenerator();

        login.setParentID(parentBack);
        assertThat(login.getParentID()).isEqualTo(parentBack);

        login.parentID(null);
        assertThat(login.getParentID()).isNull();
    }
}
