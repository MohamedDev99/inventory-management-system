package com.moeware.ims.service;

import com.moeware.ims.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseRepository repository;
}
