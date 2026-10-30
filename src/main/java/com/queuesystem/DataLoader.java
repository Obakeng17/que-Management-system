package com.queuesystem;

import com.queuesystem.model.ServiceType;
import com.queuesystem.repository.ServiceTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// adds some starter services the first time the app runs
// (change these to match your business - clinic, restaurant, bank, etc.)
@Component
public class DataLoader implements CommandLineRunner {

    private final ServiceTypeRepository serviceTypeRepository;

    public DataLoader(ServiceTypeRepository serviceTypeRepository) {
        this.serviceTypeRepository = serviceTypeRepository;
    }

    @Override
    public void run(String... args) {
        if (serviceTypeRepository.count() == 0) {
            serviceTypeRepository.save(new ServiceType("Consultation", "A"));
            serviceTypeRepository.save(new ServiceType("Pharmacy / Pickup", "B"));
            serviceTypeRepository.save(new ServiceType("Payments / Enquiries", "C"));
        }
    }
}
