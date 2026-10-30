package com.queuesystem.repository;

import com.queuesystem.model.ServiceType;
import com.queuesystem.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // all tickets with a status, oldest first (so first come first served)
    List<Ticket> findByStatusOrderByCreatedAtAsc(String status);

    // how many tickets of this service were made since the start of today
    long countByServiceTypeAndCreatedAtAfter(ServiceType serviceType, LocalDateTime since);

    // the ticket a counter is serving right now
    Optional<Ticket> findFirstByStatusAndCounterNumber(String status, Integer counterNumber);

    // how many waiting tickets are ahead of a given ticket
    long countByStatusAndCreatedAtBefore(String status, LocalDateTime time);
}
