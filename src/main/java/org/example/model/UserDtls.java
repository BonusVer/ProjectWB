package org.example.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class UserDtls {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;

    private String fullName;

    private String email;

    private String password;

    private String role;

    private String mobileNumber;

    private boolean accountNotLocked;

    private boolean enabled;

    private String verificationCode;
}
