package com.example.demo.repository;

import com.example.demo.models.Photos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photos, Long> {
    List<Photos> findAllByOrderByCreatedAtDesc();
}
