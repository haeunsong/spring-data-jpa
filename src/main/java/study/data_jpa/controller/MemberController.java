package study.data_jpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    // /members?page=0&size=3&sort=id,desc&sort=username,desc
//    @GetMapping("/members")
//    public Page<Member> list(Pageable pageable) {
//        Page<Member> page = memberRepository.findAll(pageable);
//        return page;
//    }

    @GetMapping("/members")
    public Page<MemberDto> list(Pageable pageable) {
        // return memberRepository.findAll(pageable).map(MemberDto::new);
        Page<Member> page = memberRepository.findAll(pageable);
        Page<MemberDto> pageDto = page.map(MemberDto::new);
        return pageDto;
    }
}
