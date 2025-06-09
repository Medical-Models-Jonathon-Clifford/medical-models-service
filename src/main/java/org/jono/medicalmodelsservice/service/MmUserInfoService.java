package org.jono.medicalmodelsservice.service;

import org.jono.medicalmodelsservice.model.LoginCompanies;
import org.jono.medicalmodelsservice.model.LoginUser;
import org.jono.medicalmodelsservice.repository.UserInfoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class MmUserInfoService {

    private final UserInfoRepository userInfoRepository;

    public MmUserInfoService(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    public OidcUserInfo loadUser(String username) {
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

    public String getBase64Picture(String username) {
        return this.userInfoRepository.getBase64Picture(username);
    }
}
