package com.juancasterba.taskflow_api.security.config; // O tu paquete de config

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class HibernateFilterAspect {

    private final EntityManager entityManager;

    // Este "aspecto" se ejecutará ANTES de CUALQUIER método público
    // en CUALQUIER clase dentro de tus paquetes de servicio.
    @Before("execution(public * com.juancasterba.taskflow_api.service..*.*(..))")
    public void enableActiveStatusFilter() {
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("activeStatusFilter");
    }
}