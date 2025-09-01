package com.ddiring.Backend_BlockchainConnector.utils;

import com.ddiring.Backend_BlockchainConnector.common.exception.NotFound;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestHeaderUtils {
    public static String getRequestHeaderParamAsString(String key) {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return requestAttributes.getRequest().getHeader(key);
    }

    public static String getUserSeq() {
        String userSeq = (getRequestHeaderParamAsString("userSeq"));
        if (userSeq == null) {
            throw new NotFound("사용자 번호를 찾을 수 없습니다.");
        }
        return userSeq;
    }

    public static String getRole() {
        String role = getRequestHeaderParamAsString("role");
        if (role == null) {
            return null;
        }
        return role;
    }

}
