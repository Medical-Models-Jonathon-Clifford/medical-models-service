package org.jono.medicalmodelsservice.utils;

import java.util.List;
import java.util.Optional;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.Company;
import org.jono.medicalmodelsservice.model.User;
import org.jono.medicalmodelsservice.model.dto.CommentDto;
import org.jono.medicalmodelsservice.model.dto.CompanyDto;
import org.jono.medicalmodelsservice.model.dto.UserDto;
import org.jono.medicalmodelsservice.model.dto.ViewCompanyDetailsDto;
import org.jono.medicalmodelsservice.model.dto.ViewUserDetailsDto;

public class DtoAdapters {

    private DtoAdapters() {
        // Utility class
    }

    public static List<UserDto> userToDto(final List<User> users) {
        return users.stream()
                .map(user -> new UserDto(user.getId(),
                                         fullNameOfUser(user),
                                         user.getEmail()))
                .toList();
    }

    public static String fullNameOfUser(final User user) {
        return String.format("%s %s %s", user.getHonorific(), user.getGivenName(), user.getFamilyName());
    }

    public static ViewCompanyDetailsDto companyToViewDto(final Company company) {
        return new ViewCompanyDetailsDto(company.getId(),
                                         company.getName(),
                                         company.getLocationState(),
                                         company.getLogoFilename());
    }

    public static ViewUserDetailsDto userToViewDto(final User user) {
        return new ViewUserDetailsDto(user.getId(),
                                      fullNameOfUser(user),
                                      user.getEmail(),
                                      String.format("%s.webp", user.getUsername()));
    }

    public static List<CompanyDto> companyToDto(final List<Company> companies) {
        return companies.stream()
                .map(company -> new CompanyDto(company.getId(),
                                               company.getName(),
                                               company.getLocationState()))
                .toList();
    }

    public static CommentDto commentToDto(final Comment comment, final Optional<User> user) {
        final String profilePicture = user
                .map(u -> String.format("/users/picture/%s.webp", u.getUsername()))
                .orElse(null);
        final String fullName = user
                .map(DtoAdapters::fullNameOfUser)
                .orElse(null);
        return CommentDto.builder()
                .id(comment.getId())
                .documentId(comment.getDocumentId())
                .creator(comment.getCreator())
                .body(comment.getBody())
                .createdDate(comment.getCreatedDate())
                .modifiedDate(comment.getModifiedDate())
                .profilePicturePath(profilePicture)
                .fullName(fullName)
                .build();
    }
}
