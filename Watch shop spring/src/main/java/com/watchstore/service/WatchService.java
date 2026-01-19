package com.watchstore.service;

import com.watchstore.model.Watch;
import com.watchstore.repository.WatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WatchService {

    private final WatchRepository watchRepository;

    public WatchService(WatchRepository watchRepository) {
        this.watchRepository = watchRepository;
    }

    public List<Watch> getAllActiveWatches() {
        return watchRepository.findByIsActiveTrue();
    }

    public List<Watch> findAll() {
        return watchRepository.findAll();
    }

    public Optional<Watch> findById(Long id) {
        return watchRepository.findById(id);
    }

    @Transactional
    public Watch save(Watch watch) {
        return watchRepository.save(watch);
    }

    @Transactional
    public void delete(Long id) {
        watchRepository.deleteById(id);
    }

    // ПАГИНАЦИЯ И ПОИСК: Метод возвращает страницу (Page) с результатами
    public org.springframework.data.domain.Page<Watch> getCatalog(String query,
            org.springframework.data.domain.Pageable pageable) {
        if (query != null && !query.isBlank()) {
            return watchRepository.findByNazwaContainingIgnoreCaseOrBrandContainingIgnoreCaseAndIsActiveTrue(
                    query, query, pageable);
        }
        return watchRepository.findByIsActiveTrue(pageable);
    }
}
