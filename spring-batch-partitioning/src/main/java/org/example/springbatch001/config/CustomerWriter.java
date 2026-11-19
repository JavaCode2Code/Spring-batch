package org.example.springbatch001.config;

import org.example.springbatch001.entity.Customer;
import org.example.springbatch001.repository.CustomerRepository;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerWriter implements ItemWriter<Customer> {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public void write(Chunk<? extends Customer> chunk) throws Exception {
        System.out.println("Thread name   " + Thread.currentThread().getName());
        customerRepository.saveAll(chunk);
    }
}
