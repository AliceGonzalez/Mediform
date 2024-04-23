package mediformapp.domain;

import static mediformapp.domain.ChildDataTestSamples.*;
import static mediformapp.domain.ChildTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import mediformapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChildDataTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChildData.class);
        ChildData childData1 = getChildDataSample1();
        ChildData childData2 = new ChildData();
        assertThat(childData1).isNotEqualTo(childData2);

        childData2.setId(childData1.getId());
        assertThat(childData1).isEqualTo(childData2);

        childData2 = getChildDataSample2();
        assertThat(childData1).isNotEqualTo(childData2);
    }

    @Test
    void childTest() throws Exception {
        ChildData childData = getChildDataRandomSampleGenerator();
        Child childBack = getChildRandomSampleGenerator();

        childData.setChild(childBack);
        assertThat(childData.getChild()).isEqualTo(childBack);

        childData.child(null);
        assertThat(childData.getChild()).isNull();
    }
}
