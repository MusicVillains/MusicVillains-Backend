package com.teamseven.MusicVillain.Security.OAuth;

import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Security.UserDetailsImpl;
import com.teamseven.MusicVillain.Member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Autowired
    public OAuth2UserServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        System.out.println("[DEBUG] OAuth2UserServiceImpl.loadUser(..) Entered");

        System.out.println("attributes" + super.loadUser(userRequest).getAttributes());
        OAuth2User oAuth2User = super.loadUser(userRequest);

        //여기서 attriutes를 찍어보면 모두 각기 다른 이름으로 데이터가 들어오는 것을 확인할 수 있음
        try {
            return process(userRequest, oAuth2User);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        System.out.println("[DEBUG] OAuth2UserServiceImpl.process(..) Entered");

        // userRequest에서 providerType 가져오기
        String providerType = (userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        System.out.println("providerType: " + providerType);
        //provider타입에 따라서 각각 다르게 userInfo가져온다. (가져온 필요한 정보는 OAuth2UserInfo로 동일하다)
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());

        // Member member = memberRepository.findByEmail(userInfo.getEmail());
        String expectedUserId = providerType +"_"+ userInfo.getId();
        Member member = memberRepository.findByUserId(expectedUserId);

        if (member != null) {
            System.out.println(">>> 이미 존재하는 회원이므로 별도로 가입하지 않음");
            if (providerType.equals(member.getProviderType()) == false) {
                System.out.println("[WARNING] Invalid Provider type");
                System.out.println(">>> 기존 회원의 providerType: " + member.getProviderType());
                System.out.println(">>> request의 providerType: " + providerType);
            }
         //   else  { updateMember(member, userInfo);}  // 업데이트는 나중에 적용 예정
        }
        else {
            member = createUser(userInfo, providerType);
        }

        return new UserDetailsImpl(member, user.getAttributes());

    }

    // 인증을 요청하는 사용자가 없는 회원이면 회원가입 시키고, 이미 존재하는 회원이면 회원정보를 변경사항 확인 후 회원정보를 업데이트한다.
    private Member createUser(OAuth2UserInfo oAuth2UserInfo, String providerType) {
        System.out.println("[DEBUG] OAuth2UserServiceImpl.createUser(..) Entered");

        String generatedUserId = providerType +"_"+ oAuth2UserInfo.getId();
        String generatedName = "-"; // 랜덤한 단어 조합으로 생성할 예정
//        if(oAuth2UserInfo.getEmail() != null) {
//            String generatedName = oAuth2UserInfo.getEmail().split("@")[0];
//            String generatedUserId = generatedName + "_" + providerType;
//        }
        Member member = Member.builder()
                // 사용자 이메일 정보만 가지고 가입
                .memberId(UUID.randomUUID().toString().replace("-", ""))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .providerType(providerType) // 문제있어서 임시비활성화. insert 할 때 문제생김 -> String 으로 바꿈
                .userId(generatedUserId)
                .userInfo("-")
                .name(generatedName) // 랜덤 닉네임으로 교체할 예정
                .email("-")
                .role("USER")
                .build();

        return memberRepository.save(member);
    }


}