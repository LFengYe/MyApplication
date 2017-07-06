/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.wms_system_new.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @FieldDescription 用于配置实体类字段说明信息 
 * @author LFeng
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldDescription {
    /** 
     * 字段的中文名 
     * @return 
     */
    String description() default "";
    
    String type() default "String";
    
    String operate() default "input";
}
