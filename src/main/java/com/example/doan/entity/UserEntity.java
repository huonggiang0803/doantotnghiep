package com.example.doan.entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.doan.status.GenderEnum;
import com.example.doan.status.UserStatus;
import com.example.doan.status.UserType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor 
@Entity(name = "users")
@Getter
@Setter
@Builder
 public class UserEntity extends AbstractEntity implements UserDetails{
   @Column(name = "username")
   private String userName;
    @Column(name = "fullname")
    private String fullName;
    @Column(name = "password")
    private String passWord;
    @Column(name = "address")
    private String addess;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "dateofbirth")
    // @Temporal(TemporalType.DATE): Xác định kiểu dữ liệu ngày tháng, chỉ lưu ngày (không bao gồm giờ phút).
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    @Column(name = "phonenumber")
    private String phone;
    @Enumerated(EnumType.STRING)
    // @JdbcTypeCode(SqlTypes.NAMED_ENUM): Ánh xạ Enum Java với kiểu dữ liệu ENUM trong PostgreSQL.
    @Column(name = "gender", nullable = false)
    private GenderEnum gender;

    @Column(name = "is_deleted", nullable = false)
    private Byte is_deleted = 0;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private UserType type;
    @Column(name = "fullname")
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_" + type.name())); 
    return authorities;
    }
    @Override
    public String getPassword() {
      return passWord;
    }
    @Override
    public String getUsername() {
      return userName;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
 }
