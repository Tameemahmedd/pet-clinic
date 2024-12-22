package com.example.pet_clinic.service;

import com.example.pet_clinic.model.Pet;
import com.example.pet_clinic.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {
    @Autowired
    private PetRepository petRepository;

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public void savePet(Pet pet) {
        petRepository.save(pet);
    }
}
