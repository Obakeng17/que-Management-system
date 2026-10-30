package com.queuesystem.controller;

import com.queuesystem.service.QueueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// the screen the staff use at their counter
@Controller
public class StaffController {

    private final QueueService queueService;

    public StaffController(QueueService queueService) {
        this.queueService = queueService;
    }

    @GetMapping("/staff")
    public String staffPanel(@RequestParam(defaultValue = "1") int counter, Model model) {
        model.addAttribute("counter", counter);
        model.addAttribute("current", queueService.getCurrentTicket(counter).orElse(null));
        model.addAttribute("waiting", queueService.getWaitingTickets());
        return "staff";
    }

    @PostMapping("/staff/next")
    public String callNext(@RequestParam int counter) {
        queueService.callNext(counter);
        return "redirect:/staff?counter=" + counter;
    }

    @PostMapping("/staff/done")
    public String done(@RequestParam int counter) {
        queueService.finishCurrent(counter);
        return "redirect:/staff?counter=" + counter;
    }

    @PostMapping("/staff/skip")
    public String skip(@RequestParam int counter) {
        queueService.skipCurrent(counter);
        return "redirect:/staff?counter=" + counter;
    }
}
