package com.fpt.gta.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@Service
public class TokenAuthenticationService {
    static final String TOKEN_PREFIX = "Bearer ";

    static final String HEADER_STRING = "Authorization";

    public static Authentication getAuthentication(HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authOk = new UsernamePasswordAuthenticationToken(null, null, Collections.emptyList());
        UsernamePasswordAuthenticationToken authNotOk = new UsernamePasswordAuthenticationToken(null, null);
        boolean isGetMethod = request.getMethod().equals(RequestMethod.GET.toString());
        try {
            String token = request.getHeader(HEADER_STRING);
            if (token != null) {
                if (!token.isEmpty()) {
                    token = token.replace(TOKEN_PREFIX, "");
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

//                    boolean checkRevoke = false;
//                    checkRevoke = !request.getMethod().equals(HttpMethod.GET);
                    FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(token, false);

                    request.setAttribute("FirebaseToken", firebaseToken);
                    return authOk;
                }
            }
            System.out.println("no auth header" + "-" + request.getMethod() + "-" + RequestMethod.GET + "=" + isGetMethod);
            if (isGetMethod) {
                return authOk;
            } else {
                return authNotOk;
            }
        } catch (Exception e) {
            System.out.println("Exception in filter");
            e.printStackTrace();
            if (isGetMethod) {
                return authOk;
            } else {
                return authNotOk;
            }
        }
    }
}