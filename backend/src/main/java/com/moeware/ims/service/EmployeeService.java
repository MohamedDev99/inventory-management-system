package com.moeware.ims.service;

import com.moeware.ims.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository repository;
}
