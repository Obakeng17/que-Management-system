package com.queuesystem.service;

import com.queuesystem.model.ServiceType;
import com.queuesystem.model.Ticket;
import com.queuesystem.repository.ServiceTypeRepository;
import com.queuesystem.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QueueService {

    private final TicketRepository ticketRepository;
    private final ServiceTypeRepository serviceTypeRepository;

    public QueueService(TicketRepository ticketRepository, ServiceTypeRepository serviceTypeRepository) {
        this.ticketRepository = ticketRepository;
        this.serviceTypeRepository = serviceTypeRepository;
    }

    public List<ServiceType> getAllServices() {
        return serviceTypeRepository.findAll();
    }

    // customer takes a number
    public Ticket createTicket(Long serviceId) {
        ServiceType service = serviceTypeRepository.findById(serviceId).orElseThrow();

        // numbers start again from 001 every day
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        long countToday = ticketRepository.countByServiceTypeAndCreatedAtAfter(service, startOfToday);
        int nextNumber = (int) countToday + 1;

        Ticket ticket = new Ticket();
        ticket.setServiceType(service);
        ticket.setTicketNumber(service.getPrefix() + String.format("%03d", nextNumber));
        ticket.setStatus("WAITING");
        ticket.setCreatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    public Ticket getTicket(Long id) {
        return ticketRepository.findById(id).orElseThrow();
    }

    // how many people are in front of this ticket
    public long peopleAhead(Ticket ticket) {
        if (!ticket.getStatus().equals("WAITING")) {
            return 0;
        }
        return ticketRepository.countByStatusAndCreatedAtBefore("WAITING", ticket.getCreatedAt());
    }

    public List<Ticket> getWaitingTickets() {
        return ticketRepository.findByStatusOrderByCreatedAtAsc("WAITING");
    }

    public Optional<Ticket> getCurrentTicket(int counter) {
        return ticketRepository.findFirstByStatusAndCounterNumber("SERVING", counter);
    }

    // staff presses "Call Next"
    public Optional<Ticket> callNext(int counter) {
        // if this counter is still serving someone, mark them as done first
        Optional<Ticket> current = getCurrentTicket(counter);
        if (current.isPresent()) {
            current.get().setStatus("DONE");
            ticketRepository.save(current.get());
        }

        List<Ticket> waiting = getWaitingTickets();
        if (waiting.isEmpty()) {
            return Optional.empty();
        }

        Ticket next = waiting.get(0);
        next.setStatus("SERVING");
        next.setCounterNumber(counter);
        next.setCalledAt(LocalDateTime.now());
        ticketRepository.save(next);
        return Optional.of(next);
    }

    // customer was served
    public void finishCurrent(int counter) {
        Optional<Ticket> current = getCurrentTicket(counter);
        if (current.isPresent()) {
            current.get().setStatus("DONE");
            ticketRepository.save(current.get());
        }
    }

    // customer did not show up
    public void skipCurrent(int counter) {
        Optional<Ticket> current = getCurrentTicket(counter);
        if (current.isPresent()) {
            current.get().setStatus("SKIPPED");
            ticketRepository.save(current.get());
        }
    }
}
