package com.kc.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kc.learning.model.entity.LogPrintCertificate;
import com.kc.learning.model.vo.logPrintCertificate.LogPrintCertificateExcelVO;
import org.apache.ibatis.annotations.Param;

/**
 * @author stephen qiu
 * @description 针对表【log_print_certificate(打印证书记录表)】的数据库操作Mapper
 * @createDate 2024-10-22 23:29:25
 * @Entity com.kc.learning.model.entity.LogPrintCertificate
 */
public interface LogPrintCertificateMapper extends BaseMapper<LogPrintCertificate> {

}




