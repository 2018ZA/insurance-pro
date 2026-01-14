package ru.springaio.insuranceprobackend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.springaio.insuranceprobackend.dto.JwtResponse;
import ru.springaio.insuranceprobackend.dto.LoginRequest;
import ru.springaio.insuranceprobackend.entity.User;
import ru.springaio.insuranceprobackend.repository.UserRepository;
import ru.springaio.insuranceprobackend.security.JwtTokenProvider;

// Контроллер для обработки запросов аутентификации
@RestController
// Базовый путь для всех эндпоинтов аутентификации
@RequestMapping("/api/auth")
// Автоматически генерирует конструктор с обязательными полями (final зависимости)
@RequiredArgsConstructor
public class AuthController {

    // Менеджер аутентификации Spring Security для проверки учетных данных
    private final AuthenticationManager authenticationManager;
    // Репозиторий для работы с пользователями в базе данных
    private final UserRepository userRepository;
    // Провайдер для работы с JWT токенами (генерация, валидация)
    private final JwtTokenProvider tokenProvider;

    // Обработчик POST запроса для входа пользователя в систему
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // Аутентификация пользователя с помощью Spring Security
        // Создается объект аутентификации с логином и паролем из запроса
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),  // Получение логина из DTO
                        loginRequest.getPassword()   // Получение пароля из DTO
                )
        );

        // Установка объекта аутентификации в контекст безопасности Spring
        // Это позволяет Spring Security знать, что пользователь аутентифицирован
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Генерация JWT токена на основе данных аутентификации
        String jwt = tokenProvider.generateToken(authentication);

        // Получение информации о пользователе из объекта аутентификации
        // UserDetails - стандартный интерфейс Spring Security для информации о пользователе
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        // Поиск полной информации о пользователе в базе данных по логину
        User user = userRepository.findByLogin(userDetails.getUsername()).orElseThrow();

        // Преобразование списка прав (GrantedAuthority) в список строк (ролей)
        // GrantedAuthority представляет права/роли пользователя в Spring Security
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)  // Получение названия роли/права
                .collect(Collectors.toList());        // Сбор в список

        // Возврат успешного ответа с JWT токеном и информацией о пользователе
        return ResponseEntity.ok(new JwtResponse(
                jwt,                 // Сгенерированный JWT токен
                user.getId(),        // ID пользователя из базы данных
                user.getLogin(),     // Логин пользователя
                user.getFullName(),  // Полное имя пользователя
                roles                // Список ролей пользователя
        ));
    }
}