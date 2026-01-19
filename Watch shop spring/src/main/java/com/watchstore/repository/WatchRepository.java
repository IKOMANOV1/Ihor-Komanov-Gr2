package com.watchstore.repository;

import com.watchstore.model.Watch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchRepository extends JpaRepository<Watch, Long> {
    // ПАГИНАЦИЯ: Метод для получения страницы активных часов
    org.springframework.data.domain.Page<Watch> findByIsActiveTrue(org.springframework.data.domain.Pageable pageable);

    java.util.List<Watch> findByIsActiveTrue();

    // ПОИСК И ПАГИНАЦИЯ: Поиск по названию или бренду с постраничным выводом
    org.springframework.data.domain.Page<Watch> findByNazwaContainingIgnoreCaseOrBrandContainingIgnoreCaseAndIsActiveTrue(
            String nazwa, String brand, org.springframework.data.domain.Pageable pageable);
}
