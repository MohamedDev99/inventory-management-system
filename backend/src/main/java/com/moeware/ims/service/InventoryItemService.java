package com.moeware.ims.service;

import com.moeware.ims.repository.InventoryItemRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryItemService {
    private final InventoryItemRepository repository;
}
