package org.jono.medicalmodelsservice.service;

import org.jono.medicalmodelsservice.repository.UserInfoRepository;
import org.springframework.stereotype.Service;

@Service
public class MmUserInfoService {

    private final UserInfoRepository userInfoRepository;

    public MmUserInfoService(final UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    public String getBase64Picture(final String username) {
        return this.userInfoRepository.getBase64Picture(username);
    }
}
