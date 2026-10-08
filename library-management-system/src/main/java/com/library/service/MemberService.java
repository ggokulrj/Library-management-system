package com.library.service;

import com.library.entity.Member;
import com.library.exception.BusinessException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.MemberRepository;
import com.library.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final TransactionRepository transactionRepository;

    public List<Member> getAll() { return memberRepository.findAll(); }

    public Member getById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id " + id));
    }

    public Member add(Member member) {
        if (memberRepository.findByEmail(member.getEmail()).isPresent())
            throw new BusinessException("A member with this email already exists");
        member.setId(null);
        return memberRepository.save(member);
    }

    public Member update(Long id, Member in) {
        Member member = getById(id);
        memberRepository.findByEmail(in.getEmail()).ifPresent(other -> {
            if (!other.getId().equals(id)) throw new BusinessException("Another member already uses this email");
        });
        member.setName(in.getName());
        member.setEmail(in.getEmail());
        member.setPhone(in.getPhone());
        return memberRepository.save(member);
    }

    public void delete(Long id) {
        Member member = getById(id);
        if (transactionRepository.existsByMemberId(id))
            throw new BusinessException("This member has borrowing history and cannot be deleted");
        memberRepository.delete(member);
    }
}
