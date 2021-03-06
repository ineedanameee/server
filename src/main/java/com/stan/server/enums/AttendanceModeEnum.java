package com.stan.server.enums;

import lombok.Getter;

@Getter
public enum AttendanceModeEnum {
    LOCATION(0, "位置考勤"),
    CODE(1, "口令"),
    QRCode(2, "二维码"),
    FILL(3, "补卡"),
    ;

    private Integer code;

    private String message;

    AttendanceModeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessageByCode(Integer code) {
        for (AttendanceModeEnum anEnum : AttendanceModeEnum.values()) {
            if (code.equals(anEnum.code))
                return anEnum.message;
        }
        return null;
    }
}
