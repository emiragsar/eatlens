package com.eatlens.app.userdto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterRequest {
    
    @NotBlank(message = "Email zorunludur")
    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;
    
    @NotBlank(message = "Şifre zorunludur")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır")
    private String password;
    
    @NotBlank(message = "İsim zorunludur")
    private String firstName;
    
    @NotBlank(message = "Soyisim zorunludur")
    private String lastName;
    
    private String phoneNumber;
    
    @NotBlank(message = "Rol zorunludur")
    private String role; // CUSTOMER veya RESTAURANT_OWNER
}