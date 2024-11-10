package com.kc.learning.service;

import com.kc.learning.model.vo.logPrintCertificate.LogPrintCertificateExcelVO;
import com.kc.learning.utils.WordUtils;
import org.junit.jupiter.api.Test;

public class CertificateServiceTest {
    

    @Test
    public void testGenerateCertificate() {
        // 构造一个模拟的 LogPrintCertificateExcelVO 对象
        LogPrintCertificateExcelVO certificateVO = new LogPrintCertificateExcelVO();
        certificateVO.setUserName("张三");
        certificateVO.setUserIdCard("123456789012345678");
        certificateVO.setUserGender("男");
        certificateVO.setCertificateNumber("C123456");
        certificateVO.setCourseName("Java编程课程");
        certificateVO.setAcquisitionTime("2024年11月");
        certificateVO.setFinishTime("2024年12月");

        // 调用证书生成方法
        String resultUrl = WordUtils.generateCertificate(certificateVO);
        System.out.println(resultUrl);
    }
}
