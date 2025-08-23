package org.jono.medicalmodelsservice.model;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Component;

@Component
public class MmUserBuilder {

    private final String baseUrl;

    public MmUserBuilder(@Value("${base.url}") final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Builder builder() {
        return new Builder(this.baseUrl);
    }

    public static final class Builder {

        private final String baseUrl;
        private final String picturesUrl;
        private String username;
        private String password;
        private List<String> roles;
        private String givenName;
        private String familyName;
        private String base64Picture;
        private String honorific;

        Builder(final String baseUrl) {
            this.baseUrl = baseUrl;
            this.picturesUrl = baseUrl + "/users/picture";
        }

        public Builder username(final String username) {
            this.username = username;
            return this;
        }

        public Builder password(final String password) {
            this.password = password;
            return this;
        }

        public Builder roles(final List<String> roles) {
            this.roles = roles;
            return this;
        }

        public Builder givenName(final String givenName) {
            this.givenName = givenName;
            return this;
        }

        public Builder familyName(final String familyName) {
            this.familyName = familyName;
            return this;
        }

        public Builder base64Picture(final String base64Picture) {
            this.base64Picture = base64Picture;
            return this;
        }

        public Builder honorific(final String honorific) {
            this.honorific = honorific;
            return this;
        }

        public MmUser build() {
            final LoginUser loginUser = new LoginUser(this.honorific + " " + this.givenName + " " + this.familyName,
                                                      this.username, this.password);
            final UserDetails userDetails = User.withUsername(this.username)
                    .password(this.password)
                    .roles(this.roles.toArray(new String[0]))
                    .build();
            final OidcUserInfo oidcUserInfo1 = OidcUserInfo.builder()
                    .subject(this.username)
                    .name(this.honorific + " " + this.givenName + " " + this.familyName)
                    .givenName(this.givenName)
                    .familyName(this.familyName)
                    .profile(this.baseUrl + "/" + this.username)
                    .picture(this.picturesUrl + "/" + this.username + ".png")
                    .email(this.username + "@example.com")
                    .emailVerified(true)
                    .gender("female")
                    .birthdate("1970-01-01")
                    .claim("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                    .claim("honorific", this.honorific)
                    .updatedAt("1970-01-01T00:00:00Z")
                    .build();
            return new MmUser(loginUser, userDetails, oidcUserInfo1, this.base64Picture);
        }
    }
}
