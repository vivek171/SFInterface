package com.htc.remedy.repo;

import com.htc.remedy.domain.JDBCAccounts;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JDBCAccountsRepo extends JpaRepository<JDBCAccounts, Long> {

    List<JDBCAccounts> findAllByActiveIsTrue();

    JDBCAccounts findByEnvironment(String Environment);

    List<JDBCAccounts> findByHost(String Host);

    List<JDBCAccounts> findByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContainingOrderByEnvironmentAsc(Pageable pageable, String environment, String Host, String databasename, String username);

    List<JDBCAccounts> findByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContainingOrderByHostAsc(Pageable pageable, String environment, String Host, String databasename, String username);

    List<JDBCAccounts> findByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContainingOrderByDatabaseNameAsc(Pageable pageable, String environment, String Host, String databasename, String username);

    List<JDBCAccounts> findByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContainingOrderByUsernameAsc(Pageable pageable, String environment, String Host, String databasename, String username);

    List<JDBCAccounts> findByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContainingOrderByEnvironmentDesc(Pageable pageable, String environment, String Host, String databasename, String username);

    List<JDBCAccounts> findByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContainingOrderByHostDesc(Pageable pageable, String environment, String Host, String databasename, String username);

    List<JDBCAccounts> findByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContainingOrderByDatabaseNameDesc(Pageable pageable, String environment, String Host, String databasename, String username);

    List<JDBCAccounts> findByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContainingOrderByUsernameDesc(Pageable pageable, String environment, String Host, String databasename, String username);

    List<JDBCAccounts> findAllByOrderByEnvironmentAsc(Pageable pageable);

    List<JDBCAccounts> findAllByOrderByHostAsc(Pageable pageable);

    List<JDBCAccounts> findAllByOrderByDatabaseNameAsc(Pageable pageable);

    List<JDBCAccounts> findAllByOrderByUsernameAsc(Pageable pageable);

    List<JDBCAccounts> findAllByOrderByEnvironmentDesc(Pageable pageable);

    List<JDBCAccounts> findAllByOrderByHostDesc(Pageable pageable);

    List<JDBCAccounts> findAllByOrderByDatabaseNameDesc(Pageable pageable);

    List<JDBCAccounts> findAllByOrderByUsernameDesc(Pageable pageable);

    int countByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContaining(String ConnName, String Client, String Host, String Domain);



}
