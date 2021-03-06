package com.stan.server.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("sys_user")
@ApiModel(description = "系统用户")
public class User {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "自增id")
    private Integer id;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "员工姓名")
    private String name;
    @ApiModelProperty(value = "用户密码")
    private String userPassword;
    @ApiModelProperty(value = "openId")
    private String openId;
    @ApiModelProperty(value = "phoneNumber")
    private Long phoneNumber;
    @ApiModelProperty(value = "userCode")
    private String userCode;
    @ApiModelProperty(value = "departmentId")
    private Integer departmentId;
}
