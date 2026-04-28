package com.carrental.service;

import com.carrental.common.PageResult;
import com.carrental.dto.LoginDTO;
import com.carrental.dto.LoginVO;
import com.carrental.dto.RegisterDTO;
import com.carrental.dto.UpdateMeDTO;
import com.carrental.dto.UserUpdateDTO;
import com.carrental.dto.UserVO;

public interface UserService {

    LoginVO login(LoginDTO dto);

    void register(RegisterDTO dto);

    PageResult<UserVO> listUsers(Integer page, Integer size, String keyword);

    UserVO getUserById(Long id);

    UserVO getMe(Long userId);

    UserVO updateMe(Long userId, UpdateMeDTO dto);

    void updateUser(Long id, UserUpdateDTO dto);

    void updateUserStatus(Long id, Integer status);

    void deleteUser(Long id);
}
