package com.example.pet_clinic.service;

import com.example.pet_clinic.model.Visit;
import com.example.pet_clinic.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitService {
    @Autowired
    private VisitRepository visitRepository;

    public List<Visit> getAllVisits() {
        return visitRepository.findAll();
    }

    public void saveVisit(Visit visit) {
        visitRepository.save(visit);
    }
}
