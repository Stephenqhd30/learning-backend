package com.kc.learning.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kc.learning.mapper.LogFilesMapper;
import com.kc.learning.model.entity.LogFiles;
import com.kc.learning.service.LogFilesService;
import org.springframework.stereotype.Service;

/**
* @author stephen qiu
* @description 针对表【log_files(文件上传日志记录表)】的数据库操作Service实现
* @createDate 2024-11-09 13:49:03
*/
@Service
public class LogFilesServiceImpl extends ServiceImpl<LogFilesMapper, LogFiles>
    implements LogFilesService{

}




