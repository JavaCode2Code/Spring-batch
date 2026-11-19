package org.example.springbatch001.config;

import org.example.springbatch001.entity.Customer;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public @Nullable Customer process(Customer customer) throws Exception {

       // return customer.getCountry().equalsIgnoreCase("United States") ? customer : null;
return customer;

    }
}
