package org.bookmarks.scheduler;

import java.io.IOException;
import java.util.Collection;

import org.bookmarks.repository.AccountRepository;
import org.bookmarks.domain.Customer;

import org.bookmarks.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.util.Date;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Component
public class MonthlyAccountPayment extends AbstractScheduler {

	@Autowired private EmailService emailService;

	@Autowired private AccountRepository accountRepository;

	private Logger logger = LoggerFactory.getLogger(MonthlyAccountPayment.class);

	//4am
	@Scheduled(cron = "0 00 04 * * ?")
	@Transactional
	public void process() {

		logger.info("Auto request MonthlyAccountPayment started");

		try {

			Collection<Customer>  customers = accountRepository.getAccountCustomers();

			logger.info("Have retrieved " + customers.size() + " customers with accounts");

			LocalDate today = LocalDate.now();
			LocalDate startDate = today.minus(2, ChronoUnit.MONTHS);
			LocalDate endDate = today.minus(1, ChronoUnit.MONTHS);

			logger.info("Getting total of payments between {} and {}", startDate, endDate);

			logger.info("Reseting all monthly payments to £0 (null)");

			accountRepository.resetMonthlyPayments();

			customers.stream().forEach( customer -> {
					//Get total amount deposited in a month's period, but month is last month in
					//case accounts from tsb aren't being uploaded as regularly as they should.

					BigDecimal monthlyPayment = accountRepository.getMonthlyPayment(customer, startDate, endDate);

					if(monthlyPayment == null) {
						//Could have only one payment, and it's more recent
						 monthlyPayment = accountRepository.getMonthlyPayment(customer, endDate, LocalDate.now());
					}

					Date lastPaymentDate = accountRepository.getLastPaymentDate(customer);

					Date firstPaymentDate = accountRepository.getFirstPaymentDate(customer);

				//	logger.debug("Customer {} has monthlyPayment = {}, lastPaymentDate = {}, firstPaymentDate = {}", customer, monthlyPayment, lastPaymentDate, firstPaymentDate);

					accountRepository.updateMonthlyPayments(customer, monthlyPayment, lastPaymentDate, firstPaymentDate);
			});

			logger.info("Auto request for MonthlyAccountPayment successful");

		} catch (Exception e) {
			logger.error("Error in MonthlyAccountPayment : ", e);
			emailService.sendErrorEmail(e, "MonthlyAccountPayment failed");
		}
	}

}
