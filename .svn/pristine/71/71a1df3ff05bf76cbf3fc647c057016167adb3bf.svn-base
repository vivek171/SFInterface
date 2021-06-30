package com.htc.remedy.repo;

import com.htc.remedy.domain.LDAPAccounts;
import com.htc.remedy.domain.LDAPEndPoints;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LDAPAccountsRepo extends JpaRepository<LDAPAccounts, Long> {
    List<LDAPAccounts> findByClient(String client);

    List<LDAPAccounts> findByClientAndPort(String client, int port);

    List<LDAPAccounts> findByClientAndPortAndSsoIsTrue(String client, int port);

    List<LDAPAccounts> findAllBySsoIsTrue();

    List<LDAPAccounts> findByClientAndSsoIsTrue(String client);

    LDAPAccounts findByConnName(String ConnName);

    LDAPAccounts findByLdapEndPoints(LDAPEndPoints ldapEndPoints);

    List<LDAPAccounts> findByPretextAndPosttextAndSsoIsTrue(String pretext, String posttext);


    List<LDAPAccounts> findByConnNameContainingOrClientContainingOrHostContainingOrDomainOrderByConnNameAsc(Pageable pageable, String ConnName, String Client, String Host, String Domain);

    List<LDAPAccounts> findByConnNameContainingOrClientContainingOrHostContainingOrDomainOrderByClientAsc(Pageable pageable, String ConnName, String Client, String Host, String Domain);

    List<LDAPAccounts> findByConnNameContainingOrClientContainingOrHostContainingOrDomainOrderByHostAsc(Pageable pageable, String ConnName, String Client, String Host, String Domain);

    List<LDAPAccounts> findByConnNameContainingOrClientContainingOrHostContainingOrDomainOrderByDomainAsc(Pageable pageable, String ConnName, String Client, String Host, String Domain);


    List<LDAPAccounts> findByConnNameContainingOrClientContainingOrHostContainingOrDomainOrderByConnNameDesc(Pageable pageable, String ConnName, String Client, String Host, String Domain);

    List<LDAPAccounts> findByConnNameContainingOrClientContainingOrHostContainingOrDomainOrderByClientDesc(Pageable pageable, String ConnName, String Client, String Host, String Domain);

    List<LDAPAccounts> findByConnNameContainingOrClientContainingOrHostContainingOrDomainOrderByHostDesc(Pageable pageable, String ConnName, String Client, String Host, String Domain);

    List<LDAPAccounts> findByConnNameContainingOrClientContainingOrHostContainingOrDomainOrderByDomainDesc(Pageable pageable, String ConnName, String Client, String Host, String Domain);


    List<LDAPAccounts> findAllByOrderByConnNameAsc(Pageable pageable);

    List<LDAPAccounts> findAllByOrderByClientAsc(Pageable pageable);

    List<LDAPAccounts> findAllByOrderByHostAsc(Pageable pageable);

    List<LDAPAccounts> findAllByOrderByDomainAsc(Pageable pageable);

    List<LDAPAccounts> findAllByOrderByConnNameDesc(Pageable pageable);

    List<LDAPAccounts> findAllByOrderByClientDesc(Pageable pageable);

    List<LDAPAccounts> findAllByOrderByHostDesc(Pageable pageable);

    List<LDAPAccounts> findAllByOrderByDomainDesc(Pageable pageable);

    int countByConnNameContainingOrClientContainingOrHostContainingOrDomain(String ConnName, String Client, String Host, String Domain);

    int countByClientAndSsoIsTrue(String Client);


}
