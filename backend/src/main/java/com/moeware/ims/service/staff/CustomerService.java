package com.moeware.ims.service.staff;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moeware.ims.dto.staff.customer.CustomerDTO;
import com.moeware.ims.entity.staff.Customer;
import com.moeware.ims.exception.ResourceNotFoundException;
import com.moeware.ims.exception.staff.customer.CustomerAlreadyExistsException;
import com.moeware.ims.mapper.staff.CustomerMapper;
import com.moeware.ims.repository.staff.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for Customer management
 * Implements business logic for customer operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    /**
     * Get all customers with pagination and filters
     *
     * @param customerType Filter by customer type
     * @param isActive     Filter by active status
     * @param city         Filter by city
     * @param country      Filter by country
     * @param searchTerm   Search term
     * @param pageable     Pagination information
     * @return Page of CustomerDTOs
     */
    public Page<CustomerDTO> getAllCustomers(
            String customerType,
            Boolean isActive,
            String city,
            String country,
            String searchTerm,
            Pageable pageable) {

        log.debug("Fetching customers with filters - type: {}, active: {}, city: {}, country: {}, search: {}",
                customerType, isActive, city, country, searchTerm);

        Page<Customer> customers = customerRepository.findCustomersWithFilters(
                customerType, isActive, city, country, searchTerm, pageable);

        return customers.map(customerMapper::toDTO);
    }

    /**
     * Get customer by ID
     *
     * @param id Customer ID
     * @return CustomerDTO
     * @throws ResourceNotFoundException if customer not found
     */
    public CustomerDTO getCustomerById(Long id) {
        log.debug("Fetching customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        return customerMapper.toDTO(customer);
    }

    /**
     * Get customer by customer code
     *
     * @param customerCode Customer code
     * @return CustomerDTO
     * @throws ResourceNotFoundException if customer not found
     */
    public CustomerDTO getCustomerByCode(String customerCode) {
        log.debug("Fetching customer with code: {}", customerCode);

        Customer customer = customerRepository.findByCustomerCode(customerCode)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with code: " + customerCode));

        return customerMapper.toDTO(customer);
    }

    /**
     * Create a new customer
     *
     * @param customerDTO Customer data
     * @return Created CustomerDTO
     * @throws CustomerAlreadyExistsException if customer code or email already
     *                                        exists
     */
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        log.info("Creating new customer with code: {}", customerDTO.getCustomerCode());

        // Validate unique constraints
        if (customerRepository.existsByCustomerCode(customerDTO.getCustomerCode())) {
            throw new CustomerAlreadyExistsException("code", customerDTO.getCustomerCode());
        }

        if (customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new CustomerAlreadyExistsException("email", customerDTO.getEmail());
        }

        // Validate customer type
        validateCustomerType(customerDTO.getCustomerType());

        // Set default values
        if (customerDTO.getIsActive() == null) {
            customerDTO.setIsActive(true);
        }

        Customer customer = customerMapper.toEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);

        log.info("Successfully created customer with ID: {} and code: {}", savedCustomer.getId(),
                savedCustomer.getCustomerCode());

        return customerMapper.toDTO(savedCustomer);
    }

    /**
     * Update an existing customer (full update)
     *
     * @param id          Customer ID
     * @param customerDTO Updated customer data
     * @return Updated CustomerDTO
     * @throws ResourceNotFoundException      if customer not found
     * @throws CustomerAlreadyExistsException if customer code or email conflicts
     *                                        with
     *                                        another customer
     */
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        log.info("Updating customer with ID: {}", id);

        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        // Check for duplicate customer code (excluding current customer)
        if (!existingCustomer.getCustomerCode().equals(customerDTO.getCustomerCode()) &&
                customerRepository.existsByCustomerCode(customerDTO.getCustomerCode())) {
            throw new CustomerAlreadyExistsException("code", customerDTO.getCustomerCode());
        }

        // Check for duplicate email (excluding current customer)
        if (!existingCustomer.getEmail().equals(customerDTO.getEmail()) &&
                customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new CustomerAlreadyExistsException("email", customerDTO.getEmail());
        }

        // Validate customer type
        validateCustomerType(customerDTO.getCustomerType());

        // Update all fields
        customerMapper.updateEntityFromDTO(existingCustomer, customerDTO);
        Customer updatedCustomer = customerRepository.save(existingCustomer);

        log.info("Successfully updated customer with ID: {}", id);

        return customerMapper.toDTO(updatedCustomer);
    }

    /**
     * Partially update a customer
     *
     * @param id          Customer ID
     * @param customerDTO Partial customer data
     * @return Updated CustomerDTO
     * @throws ResourceNotFoundException if customer not found
     */
    @Transactional
    public CustomerDTO patchCustomer(Long id, CustomerDTO customerDTO) {
        log.info("Partially updating customer with ID: {}", id);

        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        // Validate unique constraints if fields are being updated
        if (customerDTO.getCustomerCode() != null
                && !existingCustomer.getCustomerCode().equals(customerDTO.getCustomerCode())) {
            if (customerRepository.existsByCustomerCode(customerDTO.getCustomerCode())) {
                throw new CustomerAlreadyExistsException("code", customerDTO.getCustomerCode());
            }
        }

        if (customerDTO.getEmail() != null && !existingCustomer.getEmail().equals(customerDTO.getEmail())) {
            if (customerRepository.existsByEmail(customerDTO.getEmail())) {
                throw new CustomerAlreadyExistsException("email", customerDTO.getEmail());
            }
        }

        // Validate customer type if provided
        if (customerDTO.getCustomerType() != null) {
            validateCustomerType(customerDTO.getCustomerType());
        }

        // Update only provided fields
        customerMapper.updateEntityFromDTO(existingCustomer, customerDTO);
        Customer updatedCustomer = customerRepository.save(existingCustomer);

        log.info("Successfully patched customer with ID: {}", id);

        return customerMapper.toDTO(updatedCustomer);
    }

    /**
     * Soft delete a customer (deactivate)
     *
     * @param id Customer ID
     * @throws ResourceNotFoundException if customer not found
     */
    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Soft deleting customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        // TODO: Check if customer has pending orders before deleting
        // This would require SalesOrderRepository to be implemented

        customer.setIsActive(false);
        customerRepository.save(customer);

        log.info("Successfully deactivated customer with ID: {}", id);
    }

    /**
     * Get active customers count
     *
     * @return Number of active customers
     */
    public Long getActiveCustomerCount() {
        return customerRepository.countActiveCustomers();
    }

    /**
     * Get customer count by type
     *
     * @param customerType Customer type
     * @return Number of customers
     */
    public Long getCustomerCountByType(String customerType) {
        validateCustomerType(customerType);
        return customerRepository.countByCustomerType(customerType);
    }

    /**
     * Get retail customers
     *
     * @param pageable Pagination information
     * @return Page of retail customers
     */
    public Page<CustomerDTO> getRetailCustomers(Pageable pageable) {
        log.debug("Fetching retail customers");

        Page<Customer> customers = customerRepository.findRetailCustomers(pageable);
        return customers.map(customerMapper::toDTO);
    }

    /**
     * Get wholesale customers
     *
     * @param pageable Pagination information
     * @return Page of wholesale customers
     */
    public Page<CustomerDTO> getWholesaleCustomers(Pageable pageable) {
        log.debug("Fetching wholesale customers");

        Page<Customer> customers = customerRepository.findWholesaleCustomers(pageable);
        return customers.map(customerMapper::toDTO);
    }

    /**
     * Get corporate customers
     *
     * @param pageable Pagination information
     * @return Page of corporate customers
     */
    public Page<CustomerDTO> getCorporateCustomers(Pageable pageable) {
        log.debug("Fetching corporate customers");

        Page<Customer> customers = customerRepository.findCorporateCustomers(pageable);
        return customers.map(customerMapper::toDTO);
    }

    /**
     * Search customers by term
     *
     * @param searchTerm Search term
     * @param pageable   Pagination information
     * @return Page of matching customers
     */
    public Page<CustomerDTO> searchCustomers(String searchTerm, Pageable pageable) {
        log.debug("Searching customers with term: {}", searchTerm);

        Page<Customer> customers = customerRepository.searchCustomers(searchTerm, pageable);
        return customers.map(customerMapper::toDTO);
    }

    /**
     * Validate customer type
     *
     * @param customerType Customer type to validate
     * @throws IllegalArgumentException if invalid type
     */
    private void validateCustomerType(String customerType) {
        if (customerType == null) {
            return;
        }

        String type = customerType.toUpperCase();
        if (!type.equals("RETAIL") && !type.equals("WHOLESALE") && !type.equals("CORPORATE")) {
            throw new IllegalArgumentException("Invalid customer type. Must be RETAIL, WHOLESALE, or CORPORATE");
        }
    }
}