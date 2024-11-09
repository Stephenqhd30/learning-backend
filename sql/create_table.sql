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
    id          bigint auto_increment comment 'id'
        primary key,
    userIdCard  varchar(512)                           not null comment '身份证号',
    userName    varchar(256)                           null comment '用户昵称',
    userAvatar  varchar(1024)                          null comment '用户头像',
    userProfile varchar(512)                           null comment '用户简介',
    userGender  int          default 2                 not null comment '用户性别（0-男,1-女,2-保密）',
    userRole    varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    userPhone   varchar(256)                           null comment '手机号码',
    userEmail   varchar(256)                           null comment '用户邮箱',
    userNumber  varchar(256)                           not null comment '学号',
    createTime  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint      default 0                 not null comment '是否删除',
    constraint user_pk
        unique (userIdCard),
    constraint user_pk_2
        unique (userNumber)
)
    comment '用户' collate = utf8mb4_unicode_ci;

create index user_userName_index
    on user (userName);

-- 证书表
create table certificate
(
    id                   bigint auto_increment comment 'id'
        primary key,
    certificateNumber    varchar(512)                       not null comment '证书编号',
    certificateName      varchar(512)                       not null comment '证书名称',
    certificateType      tinyint                            not null comment '证书类型(0-干部培训,1-其他)',
    certificateYear      varchar(128)                       not null comment '证书获得时间',
    certificateSituation int      default 1                 not null comment '证书获得情况(0-有,1-没有)',
    certificateUrl       varchar(512)                       null comment '证书地址',
    reviewStatus         int      default 0                 not null comment '证书状态(0-待审核,1-通过,2-拒绝)',
    reviewMessage        varchar(512)                       null comment '审核信息',
    reviewerId           bigint                             null comment '审核人信息',
    reviewTime           datetime default CURRENT_TIMESTAMP not null comment '审核时间',
    gainUserId           bigint                             not null comment '获得人id',
    userId               bigint                             not null comment '创建用户id',
    createTime           datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime           datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    isDelete             tinyint  default 0                 not null comment '是否删除(0-正常,1删除)',
    constraint certificate_pk
        unique (certificateNumber),
    constraint certificate_user_id_fk
        foreign key (gainUserId) references user (id),
    constraint certificate_user_id_fk_2
        foreign key (userId) references user (id),
    constraint certificate_user_id_fk_3
        foreign key (reviewerId) references user (id)
)
    comment '证书表';

create index certificate_certificateName_index
    on certificate (certificateName);

create index certificate_certificateNumber_index
    on certificate (certificateNumber);

-- 用户证书表
create table user_certificate
(
    id                bigint auto_increment comment 'id'
        primary key,
    userId            bigint                             not null comment '用户id',
    certificateId     bigint                             not null comment '证书id',
    gainTime          varchar(128)                       not null comment '获得时间',
    certificateNumber varchar(256)                       not null comment '证书编号',
    certificateName   varchar(256)                       not null comment '证书名称',
    gainUserName      varchar(256)                       not null comment '获得人姓名',
    createTime        datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint user_certificate_pk
        unique (userId, certificateId),
    constraint user_certificate_certificate_id_fk
        foreign key (certificateId) references certificate (id),
    constraint user_certificate_user_id_fk
        foreign key (userId) references user (id)
)
    comment '用户证书表';


-- 课程表
create table course
(
    id              bigint auto_increment comment 'id'
        primary key,
    courseNumber    int                                not null comment '课程号',
    courseName      varchar(256)                       not null comment '课程名称',
    userId          bigint                             not null comment '创建用户id',
    acquisitionTime datetime                           null comment '开课时间',
    finishTime      datetime                           null comment '结课时间',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除(0-未删除，1-删除)',
    constraint course_number
        unique (courseNumber)
)
    comment '课程表';

-- 用户课程表(硬删除)
create table user_course
(
    id           bigint auto_increment comment 'id'
        primary key,
    userId       bigint                             not null comment '用户id',
    courseId     bigint                             not null comment '课程id',
    createUserId mediumtext                         not null comment '创建人id',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint user_course_pk
        unique (userId, courseId),
    constraint user_course_course_id_fk
        foreign key (courseId) references course (id),
    constraint user_course_user_id_fk
        foreign key (userId) references user (id)
)
    comment '用户课程表(硬删除)';


-- 打印证书记录表
create table log_print_certificate
(
    id              bigint auto_increment comment 'id'
        primary key,
    userId          bigint                   not null comment '用户id',
    certificateId   bigint                   not null comment '证书id',
    courseId        bigint                   not null comment '课程id',
    acquisitionTime datetime                 not null comment '开课时间',
    finishTime      datetime                 not null comment ' 证书发放时间',
    createUserId    bigint                   not null comment '创建人id',
    createTime      datetime default (now()) not null comment '创建时间',
    constraint log_print_certificate_certificate_id_fk
        foreign key (certificateId) references certificate (id),
    constraint log_print_certificate_course_id_fk
        foreign key (courseId) references course (id),
    constraint log_print_certificate_user_id_fk
        foreign key (userId) references user (id)
)
    comment '打印证书记录表';

-- 文件上传日志记录表
create table log_files
(
    id               bigint auto_increment comment 'id'
        primary key,
    fileKey          varchar(255)                        not null comment '文件唯一摘要值',
    fileName         varchar(255)                        not null comment '文件存储名称',
    fileOriginalName varchar(255)                        not null comment '文件原名称',
    fileSuffix       varchar(255)                        not null comment '文件扩展名',
    fileSize         bigint                              not null comment '文件大小',
    fileUrl          varchar(255)                        not null comment '文件地址',
    fileOssType      varchar(20)                         not null comment '文件OSS类型',
    createTime       datetime  default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime       timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete         tinyint   default 0                 not null comment '逻辑删除（0表示未删除，1表示已删除）',
    constraint log_files_pk
        unique (fileKey)
)
    comment '文件上传日志记录表' collate = utf8mb4_general_ci
                                 row_format = DYNAMIC;
