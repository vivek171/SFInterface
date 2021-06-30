package com.htc.remedy.repo;

import com.htc.remedy.domain.EndPointDomain;
import com.htc.remedy.domain.FieldsDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Created by kvivek on 11/3/2017.
 */
@Repository
public interface FieldRepo  extends JpaRepository<FieldsDomain,Long>{

    Set<FieldsDomain> findByFieldsEndpoint(EndPointDomain endPointDomain);
}
