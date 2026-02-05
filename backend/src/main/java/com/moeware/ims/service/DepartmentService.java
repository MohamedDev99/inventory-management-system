package com.moeware.ims.service;

import com.moeware.ims.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository repository;
}
