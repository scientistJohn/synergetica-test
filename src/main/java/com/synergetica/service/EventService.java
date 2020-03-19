package com.synergetica.service;

import com.synergetica.model.Road;
import com.synergetica.repository.EventRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventService {

    EventRepository repository;

    public Page<String> getRoadNames(Pageable pageable) {
        return repository.findAll(pageable)
                .map(Road::getName);
    }
}
