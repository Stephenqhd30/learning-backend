# 数据库初始化
# @author stephen qiu
#

-- 创建库
create database if not exists learning;

-- 切换库
use learning;

-- 用户表
create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    userName  varchar(256)                           not null comment '账号',
    userIdCard varchar(512)                           not null comment '密码',
    userPhone    varchar(256)                           null comment '手机号码',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userGender   int          default 2                 not null comment '用户性别（0-男,1-女,2-保密）',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    userEmail    varchar(256)                           null comment '用户邮箱',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
)
    comment '用户' collate = utf8mb4_unicode_ci;

create table certificate
(
    id                   bigint auto_increment comment 'id'
        primary key,
    certificateNumber        varchar(512)                       not null comment '证书编号',
    certificateName      varchar(512)                       not null comment '证书名称',
    certificateType      tinyint                            not null comment '证书类型(0-干部培训,1-其他)',
    certificateYear      varchar(128)                       not null comment '证书获得时间',
    certificateSituation int      default 1                 not null comment '证书获得情况(0-有,1-没有)',
    certificateUrl       varchar(512)                       not null comment '证书地址',
    reviewStatus         int      default 0                 not null comment '证书状态(0-待审核,1-通过,2-拒绝)',
    reviewMessage        varchar(512)                       null comment '审核信息',
    reviewerId           bigint                             null comment '审核人信息',
    reviewTime           datetime                           null comment '审核时间',
    gainUserId           bigint                             not null comment '获得人id',
    userId               bigint                             not null comment '创建用户id',
    createTime           datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime           datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    isDelete             tinyint  default 0                 not null comment '是否删除(0-正常,1删除)'
)
    comment '证书表';

create table user_certificate
(
    id              bigint auto_increment comment 'id'
        primary key,
    userId          bigint                             not null comment '用户id',
    certificateNumber   bigint                             not null comment '证书id',
    gainTime        varchar(128)                       not null comment '获得时间',
    certificateName varchar(256)                       not null comment '证书名称',
    gainUserName    varchar(256)                       not null comment '获得人姓名',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除(0-正常,1-删除)'
)
    comment '用户证书表';

create table course
(
    id           bigint auto_increment comment 'id'
        primary key,
    courseNumber int                                not null comment '课程号',
    courseName   varchar(256)                       not null comment '课程名称',
    userId       bigint                             not null comment '创建用户id',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除(0-未删除，1-删除)',
    constraint course_number
        unique (courseNumber)
)
    comment '课程表';
