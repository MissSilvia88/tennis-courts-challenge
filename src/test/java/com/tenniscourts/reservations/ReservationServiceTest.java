package com.tenniscourts.reservations;

import com.tenniscourts.schedules.Schedule;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.Scanner;
import com.tenniscourts.Schedule;
import java.util.*;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = ReservationService.class)
public class ReservationServiceTest {

    @InjectMocks
    ReservationService reservationService;

    @Silvia
  

		public static void main(String[] args) {
		Scanner sc = new Scanner (System.in);
        Schedule schedule = new Schedule();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/mm/yyyy");

        LocalDateTime startDateTime = LocalDateTime.now().plusDays(2);

        {schedule.setStartDateTime(startDateTime);
        Assert.assertEquals(reservationService.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build()), new BigDecimal(10));
       
        sc.close();
	
	

        
    }
}

