package com.watchstore.controller;

import com.watchstore.model.Watch;
import com.watchstore.service.WatchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WatchController {

    private final WatchService watchService;

    public WatchController(WatchService watchService) {
        this.watchService = watchService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Watch> recent = watchService.getAllActiveWatches().stream().limit(6).toList();
        model.addAttribute("recentWatches", recent);
        return "index";
    }

    @GetMapping("/catalog")
    public String catalog(@RequestParam(required = false) String q,
            @RequestParam(required = false) String sort,
            // ПАГИНАЦИЯ: Параметры страницы и размера
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        org.springframework.data.domain.Pageable pageable;
        if (sort != null) {
            if (sort.equals("price_asc")) {
                pageable = org.springframework.data.domain.PageRequest.of(page, size,
                        org.springframework.data.domain.Sort.by("price").ascending());
            } else if (sort.equals("price_desc")) {
                pageable = org.springframework.data.domain.PageRequest.of(page, size,
                        org.springframework.data.domain.Sort.by("price").descending());
            } else if (sort.equals("brand")) {
                pageable = org.springframework.data.domain.PageRequest.of(page, size,
                        org.springframework.data.domain.Sort.by("brand").ascending());
            } else {
                pageable = org.springframework.data.domain.PageRequest.of(page, size);
            }
        } else {
            pageable = org.springframework.data.domain.PageRequest.of(page, size);
        }

        org.springframework.data.domain.Page<Watch> watchesPage = watchService.getCatalog(q, pageable);
        model.addAttribute("watchesPage", watchesPage);
        model.addAttribute("watches", watchesPage.getContent());
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        return "catalog";
    }

    @GetMapping("/watch/{id}")
    public String viewWatch(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        Watch watch = watchService.findById(id).orElseThrow(() -> new RuntimeException("Watch not found"));
        model.addAttribute("watch", watch);
        return "watch_details";
    }
}
