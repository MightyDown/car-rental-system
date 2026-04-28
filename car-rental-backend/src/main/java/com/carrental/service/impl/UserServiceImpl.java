package com.carrental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrental.common.PageResult;
import com.carrental.common.StatusCode;
import com.carrental.dto.LoginDTO;
import com.carrental.dto.LoginVO;
import com.carrental.dto.RegisterDTO;
import com.carrental.dto.UpdateMeDTO;
import com.carrental.dto.UserUpdateDTO;
import com.carrental.dto.UserVO;
import com.carrental.entity.User;
import com.carrental.exception.BusinessException;
import com.carrental.mapper.UserMapper;
import com.carrental.service.UserService;
import com.carrental.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BusinessException(StatusCode.PARAM_ERROR, "用户名或密码错误");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(StatusCode.PARAM_ERROR, "用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(StatusCode.FORBIDDEN, "账号已被禁用，请联系管理员");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        UserVO userVO = buildVO(user);

        return LoginVO.builder()
                .token(token)
                .user(userVO)
                .build();
    }

    @Override
    public void register(RegisterDTO dto) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw BusinessException.conflict("用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setLicenseNo(dto.getLicenseNo());
        user.setRole("USER");
        user.setStatus(1);
        user.setCreditScore(100);
        userMapper.insert(user);
    }

    @Override
    public PageResult<UserVO> listUsers(Integer page, Integer size, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(User::getUsername, keyword)
                    .or()
                    .like(User::getRealName, keyword)
                    .or()
                    .like(User::getPhone, keyword));
        }
        wrapper.orderByDesc(User::getCreateTime);

        IPage<User> iPage = userMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.from(iPage.convert(this::buildVO));
    }

    @Override
    public UserVO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }
        return buildVO(user);
    }

    @Override
    public UserVO getMe(Long userId) {
        return getUserById(userId);
    }

    @Override
    public UserVO updateMe(Long userId, UpdateMeDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }
        if (dto.getRealName() != null) {
            user.setRealName(dto.getRealName());
        }
        if (dto.getLicenseNo() != null) {
            user.setLicenseNo(dto.getLicenseNo());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        userMapper.updateById(user);
        return buildVO(user);
    }

    @Override
    public void updateUser(Long id, UserUpdateDTO dto) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }
        if (dto.getRealName() != null) {
            user.setRealName(dto.getRealName());
        }
        if (dto.getLicenseNo() != null) {
            user.setLicenseNo(dto.getLicenseNo());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
        if (dto.getCreditScore() != null) {
            user.setCreditScore(dto.getCreditScore());
        }
        userMapper.updateById(user);
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }
        userMapper.deleteById(id);
    }

    private UserVO buildVO(User user) {
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .licenseNo(user.getLicenseNo())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .creditScore(user.getCreditScore())
                .createTime(user.getCreateTime())
                .build();
    }
}
