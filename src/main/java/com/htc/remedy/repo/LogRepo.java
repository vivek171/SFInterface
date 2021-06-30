package com.htc.remedy.repo;

import com.htc.remedy.domain.LogDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepo extends JpaRepository<LogDomain,Long> {
}
