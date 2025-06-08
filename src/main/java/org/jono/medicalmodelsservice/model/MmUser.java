package org.jono.medicalmodelsservice.model;

import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

@Data
public class MmUser {
    private UserDetails userDetails;
    private OidcUserInfo oidcUserInfo;
    private String base64Picture;

    public MmUser(UserDetails userDetails, OidcUserInfo oidcUserInfo, String base64Picture) {
        this.userDetails = userDetails;
        this.oidcUserInfo = oidcUserInfo;
        this.base64Picture = base64Picture;
    }
}
