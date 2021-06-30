package com.htc.remedy.repo;

import com.htc.remedy.domain.EndPointDomain;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by kvivek on 11/3/2017.
 */
@Repository
public interface EndPointRepo extends JpaRepository<EndPointDomain, Long> {

    List<EndPointDomain> findAllByActiveIsTrueAndEndPointNameIn(List<String> endpointName);

    EndPointDomain findByEndPointNameAndActiveIsTrue(String endPointName);


    EndPointDomain findByEndPointKey(String endPointKey);


    EndPointDomain findByEndPointName(String endPointName);


    List<EndPointDomain> findByEndPointNameContainingOrFormNameContainingOrderByEndPointNameAsc(Pageable pageable, String endPointName, String formName);


    List<EndPointDomain>  findByEndPointNameContainingOrFormNameContainingOrderByFormNameAsc(Pageable pageable,String endPointName,String formName);


    List<EndPointDomain>  findByEndPointNameContainingOrFormNameContainingOrderByDateAsc(Pageable pageable,String endPointName,String formName);


    List<EndPointDomain>  findByEndPointNameContainingOrFormNameContainingOrderByActiveAsc(Pageable pageable,String endPointName,String formName);


    List<EndPointDomain>  findByEndPointNameContainingOrFormNameContainingOrderByEndPointNameDesc(Pageable pageable,String endPointName,String formName);


    List<EndPointDomain>  findByEndPointNameContainingOrFormNameContainingOrderByFormNameDesc(Pageable pageable,String endPointName,String formName);
    List<EndPointDomain>  findByEndPointNameContainingOrFormNameContainingOrderByDateDesc(Pageable pageable,String endPointName,String formName);
    List<EndPointDomain>  findByEndPointNameContainingOrFormNameContainingOrderByActiveDesc(Pageable pageable,String endPointName,String formName);

    List<EndPointDomain>  findAllByOrderByEndPointNameAsc(Pageable pageable);
    List<EndPointDomain>  findAllByOrderByFormNameAsc(Pageable pageable);
    List<EndPointDomain>  findAllByOrderByDateAsc(Pageable pageable);
    List<EndPointDomain>  findAllByOrderByActiveAsc(Pageable pageable);

    List<EndPointDomain>  findAllByOrderByEndPointNameDesc(Pageable pageable);
    List<EndPointDomain>  findAllByOrderByFormNameDesc(Pageable pageable);
    List<EndPointDomain>  findAllByOrderByDateDesc(Pageable pageable);
    List<EndPointDomain>  findAllByOrderByActiveDesc(Pageable pageable);

    int countByEndPointNameContainingOrFormNameContaining(String endPointName,String formName);



}
