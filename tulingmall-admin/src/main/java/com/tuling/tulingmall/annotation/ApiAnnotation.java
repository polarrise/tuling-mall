package com.tuling.tulingmall.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.TYPE,ElementType.METHOD})
@Documented
public @interface ApiAnnotation {
}
