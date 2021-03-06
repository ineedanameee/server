package com.stan.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stan.server.entity.AttendanceRecord;
import com.stan.server.entity.AttendanceRules;
import com.stan.server.entity.AttendanceRulesTime;
import com.stan.server.enums.AttendanceModeEnum;
import com.stan.server.enums.RecordTypeEnum;
import com.stan.server.enums.StatusEnum;
import com.stan.server.model.vo.UserVO;
import com.stan.server.service.*;
import com.stan.server.utils.LocationUtils;
import com.stan.server.utils.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class SignInServiceImpl implements SignInService {

    @Autowired
    private AttendanceRulesService attendanceRulesService;
    @Autowired
    private AttendanceRulesTimeService attendanceRulesTimeService;
    @Autowired
    private AttendanceRecordService attendanceRecordService;
    @Autowired
    private UserService userService;

    @Override
    public ResultVO<Object> signIn(String openId, AttendanceModeEnum modeEnum, double longitude, double latitude, String address, Integer type) {
        LocalDateTime now = LocalDateTime.now();
        if (!attendanceRulesTimeService.needWorking(now.toLocalDate())) {
            return ResultVO.fail("不需要考勤");
        }
        // 进行考勤
        AttendanceRules attendanceRules = attendanceRulesService.getByAttendanceWay(modeEnum.getCode());
        if (attendanceRules == null) {
            return ResultVO.fail("不支持该考勤方式或者不需要考勤");
        }
        // 判断位置，口令考勤不需要判断
        if (!AttendanceModeEnum.CODE.equals(modeEnum)) {
            double distance = LocationUtils.getDistance(longitude, latitude, attendanceRules.getLon(), attendanceRules.getLat());
            if (distance < attendanceRules.getDistance()) {
                return ResultVO.fail("考勤距离超出范围");
            }
        }
        // 考勤记录
        AttendanceRecord attendanceRecord = new AttendanceRecord();
        // 判断时间
        LocalTime localTime = now.toLocalTime();
        if (type.equals(RecordTypeEnum.BEGIN_WORKING.getCode())) {
            // 是否是上班最晚打卡时间
            boolean beginEndTime = localTime.compareTo(attendanceRules.getBeginWorkEndTime()) > 0;
            if (beginEndTime) {
                return ResultVO.fail("上班打卡时间已过，请补卡");
            }
        } else if (type.equals(RecordTypeEnum.END_WORKING.getCode())) {
            // 是否是上班最晚打卡时间
            boolean endEndTime = localTime.compareTo(attendanceRules.getEndWorkEndTime()) > 0;
            if (endEndTime) {
                return ResultVO.fail("下班打卡时间已过，请补卡");
            }
        } else {
            return ResultVO.fail("暂时不支持该考勤方式");
        }
        attendanceRecord.setAttendanceTime(now);
        UserVO userInfo = userService.getUserInfoByOpenId(openId);
        attendanceRecord.setUserId(userInfo.getId());
        attendanceRecord.setUserName(userInfo.getName());
        attendanceRecord.setAttendanceMode(modeEnum.getCode());
        attendanceRecord.setLocationLat(latitude);
        attendanceRecord.setLocationLon(longitude);
        attendanceRecord.setAddress(address);
        attendanceRecord.setType(type);
        attendanceRecord.setUserName(userInfo.getName());

        attendanceRecordService.save(attendanceRecord);
        return ResultVO.success();
    }
}
