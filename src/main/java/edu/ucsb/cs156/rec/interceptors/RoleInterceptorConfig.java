package edu.ucsb.cs156.rec.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class RoleInterceptorConfig implements WebMvcConfigurer {
    @Autowired
    RoleInterceptor roleAdminProfessorInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(roleAdminProfessorInterceptor);
    }
}
