package com.example.deliveryapp.util;

import com.example.deliveryapp.model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SesionUtil {

    public static HttpSession getSession() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest().getSession();
        }
        return null;
    }

    public static void setUserInSession(Usuario usuario) {
        HttpSession session = getSession();
        if (session != null) {
            session.setAttribute("usuario", usuario);
        }
    }

    public static Usuario getUserFromSession() {
        HttpSession session = getSession();
        if (session != null) {
            return (Usuario) session.getAttribute("usuario");
        }
        return null;
    }

    public static void removeUserFromSession() {
        HttpSession session = getSession();
        if (session != null) {
            session.removeAttribute("usuario");
        }
    }

    public static boolean isUserLoggedIn() {
        return getUserFromSession() != null;
    }

    public static boolean isUserInRole(String rol) {
        Usuario usuario = getUserFromSession();
        if (usuario != null && usuario.getRol() != null) {
            return rol.equals(usuario.getRol().getNombre());
        }
        return false;
    }
    
}
