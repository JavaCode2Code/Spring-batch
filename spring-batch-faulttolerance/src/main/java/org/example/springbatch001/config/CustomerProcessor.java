package org.example.springbatch001.config;

import org.example.springbatch001.entity.Customer;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer customer) throws Exception {
        if (customer == null
                || customer.getCountry() == null
                || customer.getCountry().trim().isEmpty()) {
            return null;
        }
return  customer;
    }
}
