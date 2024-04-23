package mediformapp.domain;

import static mediformapp.domain.ChildTestSamples.*;
import static mediformapp.domain.ChildVisitsTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import mediformapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChildVisitsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChildVisits.class);
        ChildVisits childVisits1 = getChildVisitsSample1();
        ChildVisits childVisits2 = new ChildVisits();
        assertThat(childVisits1).isNotEqualTo(childVisits2);

        childVisits2.setId(childVisits1.getId());
        assertThat(childVisits1).isEqualTo(childVisits2);

        childVisits2 = getChildVisitsSample2();
        assertThat(childVisits1).isNotEqualTo(childVisits2);
    }

    @Test
    void childTest() throws Exception {
        ChildVisits childVisits = getChildVisitsRandomSampleGenerator();
        Child childBack = getChildRandomSampleGenerator();

        childVisits.setChild(childBack);
        assertThat(childVisits.getChild()).isEqualTo(childBack);

        childVisits.child(null);
        assertThat(childVisits.getChild()).isNull();
    }
}
