package com.example.pet_clinic.service;

import com.example.pet_clinic.model.Owner;
import com.example.pet_clinic.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerService {
    @Autowired
    private OwnerRepository ownerRepository;

    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }

    public void saveOwner(Owner owner) {
        ownerRepository.save(owner);
    }
}
