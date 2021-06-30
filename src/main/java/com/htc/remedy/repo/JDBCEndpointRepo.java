package com.htc.remedy.repo;

import com.htc.remedy.domain.JDBCEndpoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JDBCEndpointRepo extends JpaRepository<JDBCEndpoints, Long> {

    JDBCEndpoints findByEndpointName(String endpointname);
    JDBCEndpoints findByEndpointNameAndActiveIsTrue(String endpointname);
}
