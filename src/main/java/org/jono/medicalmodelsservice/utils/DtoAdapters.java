package org.jono.medicalmodelsservice.utils;

import java.util.List;
import org.jono.medicalmodelsservice.model.Company;
import org.jono.medicalmodelsservice.model.User;
import org.jono.medicalmodelsservice.model.dto.UserDto;
import org.jono.medicalmodelsservice.model.dto.ViewCompanyDetailsDto;

public class DtoAdapters {
    public static List<UserDto> userToDto(final List<User> users) {
        return users.stream()
                .map(user -> new UserDto(user.getId(),
                                         user.getName(),
                                         user.getEmail()))
                .toList();
    }

    public static ViewCompanyDetailsDto companyToViewDto(final Company company) {
        return new ViewCompanyDetailsDto(company.getId(),
                                         company.getName(),
                                         company.getLocationState(),
                                         company.getLogoFilename());
    }
}
