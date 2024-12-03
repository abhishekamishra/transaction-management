package com.infy.transactionManagement.serviceImpl;

import com.infy.transactionManagement.exception.CalculateDiscountPointsException;
import com.infy.transactionManagement.exception.MonthlyDiscountPointsArithmeticException;
import com.infy.transactionManagement.exception.CustomerNotFoundException;
import com.infy.transactionManagement.dto.CustomerDto;
import com.infy.transactionManagement.dto.TransactionDetailsDto;
import com.infy.transactionManagement.dto.TransactionDto;
import com.infy.transactionManagement.entity.Customer;
import com.infy.transactionManagement.entity.Transaction;
import com.infy.transactionManagement.exception.MonthlyDiscountPointsException;
import com.infy.transactionManagement.repository.CustomerRepository;
import com.infy.transactionManagement.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Optional<CustomerDto> getCustomerDetails(Long customerId) {
        try {
            Optional<Customer> customer = customerRepository.findByCustomerId(customerId);
            if (customer.isPresent()) {
                log.debug("Customer details: " + customer);
                return getCustomerDto(customer.get());
            }
        } catch (Exception exception) {
            log.error("Error occurred while fetching customer details with customer id: " + customerId + " as " + exception.getLocalizedMessage());
            throw new CustomerNotFoundException(exception.getLocalizedMessage());
        }

        return Optional.empty();
    }

    private Optional<CustomerDto> getCustomerDto(Customer customer) {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(customer.getId() == null ? null : customer.getId());
        customerDto.setCustomerName(customer.getCustomerName() == null ? null : customer.getCustomerName());
        customerDto.setCustomerId(customer.getCustomerId() == null ? null : customer.getCustomerId());
        customerDto.setTransactions(customer.getTransactions() == null ? null : getTransactionsDto(customer.getTransactions()));

        return Optional.of(customerDto);
    }

    @Override
    public Optional<List<CustomerDto>> saveCustomerDetails(List<CustomerDto> customerDtos) {
        List<Customer> customerDetails = customerRepository.saveAllAndFlush(createCustomer(customerDtos));
        List<CustomerDto> customerDtoList = customerDetails.stream().map(cust -> new CustomerDto(cust.getId(), cust.getCustomerName(), cust.getCustomerId(), getTransactionsDto(cust.getTransactions()))).toList();
        return Optional.of(customerDtoList);
    }

    private List<Customer> createCustomer(List<CustomerDto> customerDtos) {

        List<Customer> customers = new ArrayList<>();
        for (CustomerDto customerDto : customerDtos) {
            Customer customer = new Customer();
            customer.setCustomerId(Double.valueOf(generateRandomNumber()).longValue());
            customer.setCustomerName(customerDto.getCustomerName() == null ? null : customerDto.getCustomerName());
            customer.setTransactions(createTransaction(customerDto.getTransactions(), customer));
            customers.add(customer);
        }
        return customers;
    }

    private Set<Transaction> createTransaction(Set<TransactionDto> transactionDtos, Customer customer) {

        Set<Transaction> transactions = new LinkedHashSet<>();
        for (TransactionDto transactionDto : transactionDtos) {
            Transaction transaction = new Transaction();
            transaction.setMonth(transactionDto.getMonth() == null ? null : transactionDto.getMonth());
            transaction.setAmount(transactionDto.getAmount() == null ? null : transactionDto.getAmount());
            transaction.setCustomer(customer);
            transactions.add(transaction);
        }
        return transactions;
    }

    public double generateRandomNumber() {
        double max = Math.pow(10, 3) - 1;
        double min = Math.pow(10, 1 - 1);
        double range = max - min + 1;
        return (int) (Math.random() * range) + min;
    }

    private Set<TransactionDto> getTransactionsDto(Set<Transaction> transactions) {
        return transactions.stream().map(t -> new TransactionDto(t.getId(), t.getMonth(), t.getAmount(), t.getCustomer().getCustomerId())).collect(Collectors.toSet());
    }

    @Override
    public Optional<TransactionDetailsDto> calculateDiscountPoints(Long customerId) {
        log.info("Calculation of discount points started for customer id: " + customerId);
        Optional<Customer> customer = Optional.empty();
        try {
            customer = customerRepository.findByCustomerId(customerId);
            if (customer.isPresent()) {
                log.debug("Customer details: " + customer);
            }
        } catch (Exception exception) {
            log.error("Error occurred while fetching customer details with customer id: " + customerId + " as " + exception.getLocalizedMessage());
            throw new CustomerNotFoundException();
        }

        TransactionDetailsDto transactionDetailsDto = new TransactionDetailsDto();
        Long monthlyAmount = 0l;
        List<Map<String, Long>> monthlyAmounts = new ArrayList<>();
        Map<String, Long> map = new LinkedHashMap<>();

        try {
            if (customer.isPresent() && !customer.get().getTransactions().isEmpty()) {
                // sorting transactions details based on id
                Set<Transaction> transactions = customer.get().getTransactions().stream().sorted(Comparator.comparing(Transaction::getId)).collect(Collectors.toCollection(LinkedHashSet::new));
                for (Transaction transaction : transactions) {
                    monthlyAmount = calculateMonthlyDiscountPoints(transaction).get();
                    // Monthly amount calculated
                    if (monthlyAmount != null || monthlyAmount != 0l) {
                        log.debug("Month: " + transaction.getMonth() + " amount: " + monthlyAmount);
                        map.put(transaction.getMonth(), monthlyAmount);
                    }
                }
                monthlyAmounts.add(map);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new CalculateDiscountPointsException(exception.getLocalizedMessage());
        }

        transactionDetailsDto.setCustomerName(customer.get().getCustomerName());
        transactionDetailsDto.setMonthlyAmount(monthlyAmounts);
        transactionDetailsDto.setQurterlyAmount(calculateQuarterlyDiscountPoints(monthlyAmounts));

        log.debug("TransactionDetailsDto: {}", transactionDetailsDto);
        return Optional.of(transactionDetailsDto);
    }

    /***
     * Quarterly discount points calculated
     * @param monthlyAmounts
     * @return Map<String, Long>
     */
    private Map<String, Long> calculateQuarterlyDiscountPoints(List<Map<String, Long>> monthlyAmounts) {

        List<Long> qurterlyAmounts = new ArrayList<>();
        Long qurterlyAmount = 0l;
        int limit = 1;
        Map<String, Long> map = monthlyAmounts.get(0);

        for (Map.Entry<String, Long> entry : map.entrySet()) {
            // if quarterly month count is grater than 3 (i.e. 4), grater than 6 (i.e. 7), grater than 9 (i.e. 10) then we'll create a new quarter
            if (limit == 4 || limit == 7 || limit == 10) {
                qurterlyAmounts.add(qurterlyAmount);
            }
            qurterlyAmount = qurterlyAmount + entry.getValue();
            limit++;
        }
        qurterlyAmounts.add(qurterlyAmount);
        Map<String, Long> result = new LinkedHashMap<>();
        int i = 1;
        for (Long value : qurterlyAmounts) {
            result.put("Quarter:" + i + " ", value);
            log.debug("Quarter:" + i + ", Qurterly amount:", value);
            i++;
        }
        return result;
    }

    private Optional<Long> calculateMonthlyDiscountPoints(Transaction transaction) {

        Long monthlyDiscountPoints = 0l;

        try {
            Double amount = transaction.getAmount();

            if (amount != 0.00 && amount != null) {
                // A customer receives 2 points for every dollar spent over $100 in each transaction
                if (amount > 100.00) {
                    monthlyDiscountPoints = (long) ((Math.floor(amount - 100.00)) * 2);
                }
                // 1 point for every dollar spent between $50 and $100 in each transaction
                // $120 = 2*$20 +1*$50 = 90 pts
                if (amount >= 100.00) {
                    monthlyDiscountPoints = monthlyDiscountPoints + (1 * 50);
                }
                if (amount >= 50.00 && amount < 100.00) {
                    monthlyDiscountPoints = (long) (monthlyDiscountPoints + (1 * (Math.floor(100.00 - amount))));
                }
            }
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            throw new NullPointerException(exception.getLocalizedMessage());
        } catch (ArithmeticException exception) {
            exception.printStackTrace();
            throw new MonthlyDiscountPointsArithmeticException(exception.getLocalizedMessage());
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new MonthlyDiscountPointsException(exception.getLocalizedMessage());
        }
        log.debug("Monthly discount points: {}", monthlyDiscountPoints);
        return Optional.of(monthlyDiscountPoints);
    }
}
