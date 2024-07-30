package com.example.qnacomunity.security;

import com.example.qnacomunity.dto.response.MemberResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserDetail extends UserDetails {

  MemberResponse getMemberResponse();
}
