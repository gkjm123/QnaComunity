package com.example.qnacomunity.security;

import com.example.qnacomunity.entity.Member;
import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserDetail extends UserDetails {

  Member getMember();
}
