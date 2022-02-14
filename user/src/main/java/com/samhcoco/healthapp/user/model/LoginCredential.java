package com.samhcoco.healthapp.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class LoginCredential {
    // todo - remove
    private String username;
    private String password;
}
