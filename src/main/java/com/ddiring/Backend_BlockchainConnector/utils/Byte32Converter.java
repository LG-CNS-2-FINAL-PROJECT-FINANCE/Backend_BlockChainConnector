package com.ddiring.Backend_BlockchainConnector.utils;

import java.nio.charset.StandardCharsets;

public class Byte32Converter {
    public static String convertBytes32ToString(byte[] bytes32Array) {
        // 바이트 배열을 UTF-8 문자열로 변환합니다.
        // 이 과정에서 문자열 끝에 널 문자가 포함될 수 있습니다.
        String fullString = new String(bytes32Array, StandardCharsets.UTF_8);

        // 문자열 끝에 있는 널 문자(null terminator)를 찾아서 제거합니다.
        int nullIndex = fullString.indexOf('\u0000');
        if (nullIndex >= 0) {
            // 널 문자가 발견되면, 그 이전까지의 부분 문자열을 반환합니다.
            return fullString.substring(0, nullIndex);
        }

        // 널 문자가 없으면 전체 문자열을 반환합니다.
        return fullString;
    }

}
