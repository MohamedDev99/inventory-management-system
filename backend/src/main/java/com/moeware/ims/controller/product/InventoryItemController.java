package com.moeware.ims.controller.product;

import com.moeware.ims.service.InventoryItemService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/inventoryitems")
@RequiredArgsConstructor
public class InventoryItemController {
    private final InventoryItemService service;
}
