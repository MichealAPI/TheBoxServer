package it.mikeslab.thebox.config;

import it.mikeslab.thebox.entity.User;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthenticatedUserResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        System.out.println("RESOLVING USER");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        System.out.println("AUTH PRINCIPAL: " + auth.getPrincipal());

        Object objUser = (auth != null) ? auth.getPrincipal() :  null;

        System.out.println("AUTHENTICATED USER RESOLVER: " + objUser);
        System.out.println("AUTH PRINCIPAL: " + auth.getPrincipal());

        if (objUser instanceof User user) {
            return user;
        }

        return null;
    }

}
