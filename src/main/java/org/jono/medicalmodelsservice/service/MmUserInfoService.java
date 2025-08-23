package org.jono.medicalmodelsservice.service;

import java.util.Collection;
import java.util.List;
import org.jono.medicalmodelsservice.model.LoginCompanies;
import org.jono.medicalmodelsservice.model.LoginUser;
import org.jono.medicalmodelsservice.repository.UserInfoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;

@Service
public class MmUserInfoService {

    private final UserInfoRepository userInfoRepository;

    public MmUserInfoService(final UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    public OidcUserInfo loadUser(final String username) {
        return new OidcUserInfo(this.userInfoRepository.findByUsername(username).getOidcUserInfo().getClaims());
    }

    public List<LoginUser> getLoginUsers() {
        return this.userInfoRepository.getLoginUsers();
    }

    public Collection<UserDetails> getUserDetails() {
        return this.userInfoRepository.getUserDetails();
    }

    public List<LoginCompanies> getLoginCompanies() {
        return this.userInfoRepository.getLoginCompanies();
    }

    public String getBase64Picture(final String username) {
        return this.userInfoRepository.getBase64Picture(username);
    }
}
