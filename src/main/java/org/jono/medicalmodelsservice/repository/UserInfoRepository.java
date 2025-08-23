package org.jono.medicalmodelsservice.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jono.medicalmodelsservice.model.LoginCompanies;
import org.jono.medicalmodelsservice.model.LoginUser;
import org.jono.medicalmodelsservice.model.MmUser;
import org.jono.medicalmodelsservice.model.MmUserBuilder;
import org.jono.medicalmodelsservice.utils.ResourceUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public class UserInfoRepository {

    private final Map<String, MmUser> userInfo = new HashMap<>();
    private final List<LoginCompanies> companyInfo = new ArrayList<>();

    public MmUser findByUsername(final String username) {
        return this.userInfo.get(username);
    }

    public List<LoginUser> getLoginUsers() {
        return userInfo.values().stream().map(MmUser::getLoginUser).toList();
    }

    public Collection<UserDetails> getUserDetails() {
        return userInfo.values().stream().map(MmUser::getUserDetails).toList();
    }

    public List<LoginCompanies> getLoginCompanies() {
        return companyInfo;
    }

    public String getBase64Picture(final String username) {
        return userInfo.get(username).getBase64Picture();
    }

    public UserInfoRepository(final MmUserBuilder mmUserBuilder) throws IOException {
        final MmUser rtrenneman = mmUserBuilder.builder()
                .username("rtrenneman")
                .password("N78S9x9ft$HFGMrf")
                .honorific("Mr.")
                .givenName("Roy")
                .familyName("Trenneman")
                .roles(List.of("SUPPORT"))
                .base64Picture(getAvatar("images/it-crowd-roy-1.png"))
                .build();
        final MmUser mmoss = mmUserBuilder.builder().username(
                        "mmoss")
                .password("y?jaHKGTaji6xAd9")
                .honorific("Mr.")
                .givenName("Maurice")
                .familyName("Moss")
                .roles(List.of("SUPPORT"))
                .base64Picture(getAvatar("images/it-crowd-moss-1.png"))
                .build();
        final MmUser jbarber = mmUserBuilder.builder()
                .username("jbarber")
                .password("GM!mQn!8K8Db9p#p")
                .honorific("Ms.")
                .givenName("Jen")
                .familyName("Barber")
                .roles(List.of("SUPPORT"))
                .base64Picture(getAvatar("images/it-crowd-jen-1.png"))
                .build();
        final MmUser lcuddy = mmUserBuilder.builder()
                .username("lcuddy")
                .password("YjzJdH6!G??tntQ#")
                .honorific("Dr.")
                .givenName("Lisa")
                .familyName("Cuddy")
                .roles(List.of("ADMIN"))
                .base64Picture(getAvatar("images/cutty-profile-picture.png"))
                .build();
        final MmUser ghouse = mmUserBuilder.builder()
                .username("ghouse")
                .password("S!p5fs!MFx&&GTPs")
                .honorific("Dr.")
                .givenName("Gregory")
                .familyName("House")
                .roles(List.of("USER"))
                .base64Picture(getAvatar("images/house-md-image-2.png"))
                .build();
        final MmUser jwilson = mmUserBuilder.builder()
                .username("jwilson")
                .password("s9dQd$grL!!Y5?$h")
                .honorific("Dr.")
                .givenName("James")
                .familyName("Wilson")
                .roles(List.of("USER"))
                .base64Picture(getAvatar("images/house-wilson-image-1.png"))
                .build();
        final MmUser spotter = mmUserBuilder.builder()
                .username("spotter")
                .password("N78S9x9ft$HFGMrf")
                .honorific("Col.")
                .givenName("Sherman T.")
                .familyName("Potter")
                .roles(List.of("ADMIN"))
                .base64Picture(getAvatar("images/mash-potter-1.png"))
                .build();
        final MmUser bpierce = mmUserBuilder.builder()
                .username("bpierce")
                .password("C$At$BBGL5yLP&AM")
                .honorific("Cap.")
                .givenName("\"Hawkeye\"")
                .familyName("Pierce")
                .roles(List.of("USER"))
                .base64Picture(getAvatar("images/mash-hawkeye-1.png"))
                .build();
        final MmUser woreilly = mmUserBuilder.builder()
                .username("woreilly")
                .password("so#KKNYiqe!F5!Ph")
                .honorific("Priv.")
                .givenName("Walter \"Radar\"")
                .familyName("O’Reilly")
                .roles(List.of("USER"))
                .base64Picture(getAvatar("images/mash-radar-1.png"))
                .build();

        this.userInfo.put("rtrenneman", rtrenneman);
        this.userInfo.put("mmoss", mmoss);
        this.userInfo.put("jbarber", jbarber);
        this.userInfo.put("lcuddy", lcuddy);
        this.userInfo.put("ghouse", ghouse);
        this.userInfo.put("jwilson", jwilson);
        this.userInfo.put("spotter", spotter);
        this.userInfo.put("bpierce", bpierce);
        this.userInfo.put("woreilly", woreilly);

        this.companyInfo.add(new LoginCompanies("Medical Models Support Staff",
                                                List.of(rtrenneman.getLoginUser(),
                                                        mmoss.getLoginUser(),
                                                        jbarber.getLoginUser())));
        this.companyInfo.add(new LoginCompanies("House MD Centre for Superheroes",
                                                List.of(lcuddy.getLoginUser(),
                                                        ghouse.getLoginUser(),
                                                        jwilson.getLoginUser())));
        this.companyInfo.add(new LoginCompanies("Old Action Heroes M*A*S*H",
                                                List.of(spotter.getLoginUser(),
                                                        bpierce.getLoginUser(),
                                                        woreilly.getLoginUser())));
    }

    private String getAvatar(final String path) throws IOException {
        return ResourceUtils.loadBase64ResourceFile(path);
    }
}
