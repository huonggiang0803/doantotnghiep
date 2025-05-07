package com.example.doan.dto;

import com.example.doan.status.EnumValue;
import com.example.doan.status.GenderEnum;
import com.example.doan.status.GenderSubset;
import com.example.doan.status.UserType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.sql.Date;
import lombok.Data;

@Data
public class RegisterDTOUser {
    @NotBlank(message = "tên đăng nhập không được để trống")
    private String userName;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 3, max = 50, message = "Họ và tên phải từ 3 đến 50 ký tự")
    private String fullName;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 5, message = "Mật khẩu phải có ít nhất 5 ký tự")
    private String passWord;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 255, message = "Địa chỉ không được quá 255 ký tự")
    private String address;

    @NotBlank(message = "Email không được để trống")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Email không hợp lệ. Vui lòng nhập đúng định dạng."
    )
    private String email;

    @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
    private Date dateOfBirth;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(\\+84|0)[3-9][0-9]{8}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @GenderSubset(anyOf = {GenderEnum.NAM, GenderEnum.NỮ, GenderEnum.KHÁC})
    private GenderEnum gender;

    private Byte is_deleted;

    @NotNull(message = "type must be not null")
    @EnumValue(name = "type", enumClass = UserType.class)
    private String type;
}
