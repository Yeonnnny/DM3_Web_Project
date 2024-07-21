package net.kdigital.ec21.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.kdigital.ec21.entity.ProhibitWordEntity;

public interface ProhibitWordRepository extends JpaRepository<ProhibitWordEntity, String>{
    
}
