package com.tuling.tulingmall.aspect;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @author WangJinbiao
 * @date 2024/10/11 22：37
 */
public class RoleContext {

    private static final TransmittableThreadLocal<Boolean> hasDigitalAngelRole = new TransmittableThreadLocal<>();

    public static void setHasRole(Boolean hasRole) {
        hasDigitalAngelRole.set(hasRole);
    }

    public static Boolean getHasRole() {
        return hasDigitalAngelRole.get();
    }

    public static void clear() {
        hasDigitalAngelRole.remove();
    }

}
