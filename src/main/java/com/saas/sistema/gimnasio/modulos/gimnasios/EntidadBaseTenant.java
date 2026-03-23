package com.saas.sistema.gimnasio.modulos.gimnasios;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.util.UUID;

@MappedSuperclass
@Data
@FilterDef(name = "filtroTenant", parameters = {@ParamDef(name = "tenantId", type = UUID.class)})
@Filter(name = "filtroTenant", condition = "tenant_id = :tenantId")
public abstract class EntidadBaseTenant {

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;
}