
package org.jono.medicalmodelsservice.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.jono.medicalmodelsservice.model.Company;
import org.jono.medicalmodelsservice.model.User;
import org.jono.medicalmodelsservice.model.dto.UserDto;
import org.jono.medicalmodelsservice.model.dto.ViewCompanyDetailsDto;
import org.jono.medicalmodelsservice.model.dto.ViewUserDetailsDto;
import org.junit.jupiter.api.Test;

class DtoAdaptersTest {

    private static final User USER1 = new User("1", "john@example.com", "pic1.jpg", "john", "Dr.", "John", "Doe",
                                               LocalDateTime.now(), "password", "active");
    private static final User USER2 = new User("2", "jane@example.com", "pic2.jpg", "jane", "Ms.", "Jane", "Smith",
                                               LocalDateTime.now(), "password", "active");

    @Test
    void shouldConvertUserListToDtoList() {
        final List<User> users = List.of(USER1, USER2);

        final List<UserDto> userDtos = DtoAdapters.userToDto(users);

        assertThat(userDtos).isNotNull();
        assertThat(userDtos).hasSize(2);
        assertThat(userDtos.getFirst().id()).isEqualTo("1");
        assertThat(userDtos.getFirst().name()).isEqualTo("Dr. John Doe");
        assertThat(userDtos.getFirst().email()).isEqualTo("john@example.com");
        assertThat(userDtos.get(1).id()).isEqualTo("2");
        assertThat(userDtos.get(1).name()).isEqualTo("Ms. Jane Smith");
        assertThat(userDtos.get(1).email()).isEqualTo("jane@example.com");
    }

    @Test
    void shouldFormatFullNameOfUser() {
        final String fullName = DtoAdapters.fullNameOfUser(USER1);

        assertThat(fullName).isEqualTo("Dr. John Doe");
    }

    @Test
    void shouldConvertCompanyToViewDto() {
        final var company = Company.builder()
                .id("1")
                .name("Test Company")
                .locationState("CA")
                .logoFilename("logo.png")
                .build();

        final ViewCompanyDetailsDto dto = DtoAdapters.companyToViewDto(company);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo("1");
        assertThat(dto.name()).isEqualTo("Test Company");
        assertThat(dto.locationState()).isEqualTo("CA");
        assertThat(dto.logoFilename()).isEqualTo("logo.png");
    }

    @Test
    void shouldConvertUserToViewDto() {
        final ViewUserDetailsDto dto = DtoAdapters.userToViewDto(USER1);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo("1");
        assertThat(dto.name()).isEqualTo("Dr. John Doe");
        assertThat(dto.email()).isEqualTo("john@example.com");
        assertThat(dto.pictureFilename()).isEqualTo("john.webp");
    }
}
