package com.queuesystem.controller;

import com.queuesystem.model.Ticket;
import com.queuesystem.service.QueueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// the screen the customer uses to get a queue number
@Controller
public class KioskController {

    private final QueueService queueService;

    public KioskController(QueueService queueService) {
        this.queueService = queueService;
    }

    @GetMapping("/")
    public String kiosk(Model model) {
        model.addAttribute("services", queueService.getAllServices());
        return "kiosk";
    }

    @PostMapping("/take")
    public String takeTicket(@RequestParam Long serviceId) {
        Ticket ticket = queueService.createTicket(serviceId);
        return "redirect:/ticket/" + ticket.getId();
    }

    @GetMapping("/ticket/{id}")
    public String showTicket(@PathVariable Long id, Model model) {
        Ticket ticket = queueService.getTicket(id);
        model.addAttribute("ticket", ticket);
        model.addAttribute("ahead", queueService.peopleAhead(ticket));
        return "ticket";
    }
}
