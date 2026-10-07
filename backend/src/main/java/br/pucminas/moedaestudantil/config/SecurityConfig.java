package br.pucminas.moedaestudantil.config;

import br.pucminas.moedaestudantil.cadastro.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Locale;

@Configuration
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean UserDetailsService userDetailsService(UsuarioRepository usuarios) {
        return login -> {
            var usuario = usuarios.findByLogin(login.strip().toLowerCase(Locale.ROOT))
                    .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas."));
            return User.withUsername(usuario.getLogin()).password(usuario.getSenhaHash())
                    .roles(usuario.getPerfil()).disabled(!usuario.isAtivo()).build();
        };
    }

    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF permanece habilitado, inclusive em cadastro, login e logout.
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/auth/csrf", "/api/instituicoes").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/alunos", "/api/empresas").permitAll()
                .requestMatchers("/api/auth/me").authenticated()
                .requestMatchers("/api/alunos", "/api/alunos/**").hasRole("ALUNO")
                .requestMatchers("/api/empresas", "/api/empresas/**").hasRole("EMPRESA")
                .anyRequest().denyAll())
            .requestCache(cache -> cache.disable())
            .formLogin(form -> form.loginProcessingUrl("/api/auth/login")
                .successHandler((request, response, authentication) -> response.setStatus(200))
                .failureHandler((request, response, exception) -> {
                    response.setStatus(401); response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"mensagem\":\"Login ou senha inválidos, ou cadastro inativo.\"}");
                }).permitAll())
            .logout(logout -> logout.logoutUrl("/api/auth/logout").deleteCookies("JSESSIONID")
                .logoutSuccessHandler((request, response, authentication) -> response.setStatus(204)))
            .exceptionHandling(errors -> errors
                .authenticationEntryPoint((request, response, exception) -> {
                    response.setStatus(401); response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"mensagem\":\"Entre na sua conta para continuar.\"}");
                })
                .accessDeniedHandler((request, response, exception) -> {
                    response.setStatus(403); response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"mensagem\":\"Acesso não autorizado ou token de segurança inválido. Atualize a página.\"}");
                }))
            .build();
    }
}
