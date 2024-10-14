package com.kc.learning.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kc.learning.model.dto.course.CourseQueryRequest;
import com.kc.learning.model.entity.Course;
import com.kc.learning.model.vo.CourseVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 课程服务
 *
 * @author stephen qiu
 */
public interface CourseService extends IService<Course> {

    /**
     * 校验数据
     *
     * @param course
     * @param add 对创建的数据进行校验
     */
    void validCourse(Course course, boolean add);

    /**
     * 获取查询条件
     *
     * @param courseQueryRequest
     * @return
     */
    QueryWrapper<Course> getQueryWrapper(CourseQueryRequest courseQueryRequest);

    /**
     * 获取课程封装
     *
     * @param course
     * @param request
     * @return
     */
    CourseVO getCourseVO(Course course, HttpServletRequest request);

    /**
     * 分页获取课程封装
     *
     * @param coursePage
     * @param request
     * @return
     */
    Page<CourseVO> getCourseVOPage(Page<Course> coursePage, HttpServletRequest request);
    
    /**
     * 导入课程信息
     * @param file file
     * @return {@link Map}<{@link String}, {@link Object}> }
     */
    Map<String, Object> importCourse(MultipartFile file, HttpServletRequest request);
}