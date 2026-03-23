package com.saas.sistema.gimnasio.nucleo.configuracion;

import java.util.UUID;

public class ContextoTenant {

    private static final ThreadLocal<UUID> TENANT_ACTUAL = new ThreadLocal<>();

    public static void setTenantId(UUID tenantId) {
        TENANT_ACTUAL.set(tenantId);
    }

    public static UUID getTenantId() {
        return TENANT_ACTUAL.get();
    }

    public static void limpiar() {
        TENANT_ACTUAL.remove();
    }
}