package com.teamseven.MusicVillain;

import com.teamseven.MusicVillain.Member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class RandomNicknameGenerator {

    /*


정적인
유치한
익살스러운
자존심 센
적극적인
감상적인
수상쩍은
잘난체하는
강한
건강한
경건한
자존심 있는
완고한
믿을만한
로맨틱한
빈틈없는
포용력이 있는
명확한
깨끗한
안달하는
매력이 있는
화 잘내는
융통성이 있는
샘내는
주의 깊은
귀여운
충실한
단호한
품위 있는
순진한
기운찬
원기 왕성한
정직한
집착하는
외향적인
노련한
현실적인
조용한
수수한
단순한
현실주의 적인
감각적인
자만심이 강한
내성적인
따뜻한
우스꽝스러운
호전적인
신중한
감각이 있는
질투 많은
거만한
깔끔한
총명한
유망한
지시적인
붙임성 있는
능란한
뻔뻔스러운
얌전한
아름다운
태평스러운
고집 센
인정이 많은
정에 약한
전도 유망한
속기 쉬운
열광적인
유머러스한
열심인
편견을 가진
진지한
긍정적인
인간적인
힘찬
기운찬
확실한
재능 있는
동정심이 있는
낭만적인
기민한
재주가 많은
코믹한
확신 있는
직관적인
단정한
용기 있는
사랑스러운
냉담한
호기심이 강한
검소한
유식한
쾌활한
믿을 수 있는
친절한
호색적인
까다로운
아둔한
온화한
소심한
매력적인
엄한
용감한
흥분하기 쉬운
유치한
이기적인
희망에 차 있는
고집 센
무방비적인
동적인
성실한
열정적인
조심성 많은
옹졸한
우유부단한
감정적인
직관력 있는
악의 있는
끈기 있는
참을성 있는
튼튼한
공상적인
활발한
수줍은
자신 있는
우아한
대담한
독창적인
정정당당한
괴팍한
의지하고 있는
우호적인
우울한
무딘
편견 없는
겸손한
인자한
어린애 같은
능률적인
호인 같은
멍청한
당당한
친밀한
분별력이 있는
재치 있는
솔직담백한
굳센
신뢰할 수 있는
거드름 피우는
실질적인
애처로운
이해심이 있는
관능적인
자존심이 강한
변덕스러운
     */
    // 위에 주석한 내용 전부 하나씩 firsWords로 초기화

    public static final List<String> firstWords = Arrays.asList(
            "활동적인", "자기만족의", "독립심이 강한", "예술적인", "협동적인", "청순한", "의식이 있는", "창조적인", "경계심이 강한", "사려 깊은",
            "예속적인", "성실한", "독단적인", "관대한", "성숙한", "능력 있는", "온후한", "명랑한", "유능한", "낙천적인",
            "정적인", "유치한", "자존심 센", "적극적인", "감상적인", "수상쩍은", "잘난체하는","강한", "건강한", "경건한", "자존심 있는","완고한",
            "믿을만한", "로맨틱한", "빈틈없는", "포용력 있는", "명확한", "깨끗한", "안달하는", "매력이 있는", "화 잘내는", "융통성 있는");

    public static final List<String> secondWords = Arrays.asList(
            "소피", "엠마", "올리비아", "이자벨라", "아바", "미아", "에밀리", "아비가일", "메디슨", "엘리자베스",
            "샬롯", "에이버리", "소피아", "클로이", "엘라", "하퍼", "아멜리아", "오브리", "에디슨", "에블린");

    public static String generate() {
        Random random = new Random();
        if (firstWords.isEmpty() || secondWords.isEmpty()) {
            return "No words available for generating nickname.";
        }

        String randomFirstWord = firstWords.get(random.nextInt(firstWords.size()));
        String randomSecondWord = secondWords.get(random.nextInt(secondWords.size()));

        int randomNumber = random.nextInt(100); // Change this range if needed

        String tempNickname = randomFirstWord + randomSecondWord + randomNumber;

        /* 생성된 닉네임이 이미 존재하는지 여부는 호출하는 method 에서 확인해야함 */

        return randomFirstWord + " " + randomSecondWord + randomNumber;
    }

}
