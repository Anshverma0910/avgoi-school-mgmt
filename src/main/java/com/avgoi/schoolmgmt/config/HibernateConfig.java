package com.avgoi.schoolmgmt.config;

import com.avgoi.schoolmgmt.tenant.TenantSchemaResolver;
import com.avgoi.schoolmgmt.tenant.SchemaMultiTenantConnectionProvider;
import org.hibernate.cfg.MultiTenancySettings;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures Hibernate for schema-per-tenant: uses TenantSchemaResolver and a connection provider
 * that executes SET search_path TO &lt;tenantId&gt;, public at the start of every connection.
 */
@Configuration
public class HibernateConfig {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(
            CurrentTenantIdentifierResolver<String> tenantSchemaResolver,
            MultiTenantConnectionProvider<String> schemaMultiTenantConnectionProvider) {
        return hibernateProperties -> {
            hibernateProperties.put(MultiTenancySettings.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantSchemaResolver);
            hibernateProperties.put(MultiTenancySettings.MULTI_TENANT_CONNECTION_PROVIDER, schemaMultiTenantConnectionProvider);
        };
    }
}
