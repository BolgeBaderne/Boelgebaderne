package com.example.bolgebaderne.repository;

import com.example.bolgebaderne.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Integer> {

    List<Shift> findByUser_UserId(int userId);
}
