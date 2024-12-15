package org.jono.medicalmodelsservice.usecases;

import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.DocumentState;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class NavTreeDocInfoTest {

    @ParameterizedTest
    @EnumSource(DocumentState.class)
    public void testNavTreeDocInfoTest2(DocumentState state) {
        var testCreatedDate = LocalDateTime.of(2020, 10, 10, 10, 10);
        var testModifiedDate = LocalDateTime.of(2020, 10, 10, 10, 20);
        var testDoc = Document.builder()
                .id("1")
                .title("test title")
                .state(state)
                .body("test body")
                .body("2")
                .createdDate(testCreatedDate)
                .modifiedDate(testModifiedDate)
                .build();

        var docInfo = new NavTreeDocInfo(testDoc);

        assertThat(docInfo.getId(), is("1"));
        assertThat(docInfo.getTitle(), is("test title"));
        assertThat(docInfo.getCreatedDate(), is(testCreatedDate));
        assertThat(docInfo.getModifiedDate(), is(testModifiedDate));
    }

}