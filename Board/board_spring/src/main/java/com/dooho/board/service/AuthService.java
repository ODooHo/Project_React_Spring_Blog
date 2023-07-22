package com.dooho.board.service;

import com.dooho.board.dto.ResponseDto;
import com.dooho.board.dto.SignInDto;
import com.dooho.board.dto.SignInResponseDto;
import com.dooho.board.dto.SignUpDto;
import com.dooho.board.entity.UserEntity;
import com.dooho.board.repository.UserRepository;
import com.dooho.board.security.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenProvider tokenProvider;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ResponseDto<?> signUp(SignUpDto dto){
        String userEmail = dto.getUserEmail();
        String userPassword = dto.getUserPassword();
        String userPasswordCheck = dto.getUserPasswordCheck();


        //이메일 중복 확인
        try{
            if (userRepository.existsById(userEmail)){
                return ResponseDto.setFailed("Email already exist!");
            }
        }catch (Exception e){
            return ResponseDto.setFailed("DataBase Error!");
        }

        // 비밀번호가 다르면 failed response
        if (!userPassword.equals(userPasswordCheck)){
            return ResponseDto.setFailed("password does not matched!");
        }

        // userEntity 생성,
        UserEntity userEntity = new UserEntity(dto);
        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userPassword);
        userEntity.setUserPassword(encodedPassword);

        //repository(db)에 저장
        try{
            userRepository.save(userEntity);
        }catch (Exception e){
            return ResponseDto.setFailed("DataBase Error!");
        }

        return ResponseDto.setSuccess("Sign Up Success!",null);
    }

    public ResponseDto<SignInResponseDto> signIn(SignInDto dto){
        String userEmail = dto.getUserEmail();
        String userPassword = dto.getUserPassword();

        UserEntity userEntity = null;

        try{
            userEntity = userRepository.findByUserEmail(userEmail);
            //잘못된 이메일
            if (userEntity == null)
                return ResponseDto.setFailed("Sign in Failed");
            //잘못된 패스워드
            if (!passwordEncoder.matches(userPassword,userEntity.getUserPassword()))
                return ResponseDto.setFailed("Sign in Failed");
        }catch (Exception e){
            ResponseDto.setFailed("DataBase Error!");
        }
        userEntity.setUserPassword("");

        String token = tokenProvider.create(userEmail);
        Integer exprTime = 36000000;

        SignInResponseDto signInResponseDto = new SignInResponseDto(token,exprTime,userEntity);
        return ResponseDto.setSuccess("Sign in Success",signInResponseDto);
    }
}
