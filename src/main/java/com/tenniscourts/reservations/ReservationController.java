ReservationController.java

package com.tenniscourts.reservations;

 

import com.tenniscourts.config.BaseRestController;

import io.swagger.annotations.ApiOperation;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

 

@RestController

@RequestMapping("/reservation")

@AllArgsConstructor

public class ReservationController extends BaseRestController {

 

    private final ReservationService reservationService;

 

    @PostMapping(path = "")

    @ApiOperation(value="Book reservation", notes="Book reservation", nickname = "BookReservation")

    public ResponseEntity<Void> bookReservation(@RequestBody CreateReservationRequestDTO createReservationRequestDTO) {

        return ResponseEntity.created(locationByEntity(reservationService.bookReservation(createReservationRequestDTO).getId())).build();

    }

 

    @GetMapping(path = "", params= { "reservationId" })

    @ApiOperation(value="Find reservation by id", notes="Find reservation by id", nickname = "FindReservationById")

    public ResponseEntity<ReservationDTO> findReservation(@PathVariable("reservationId") Long reservationId) {

        return ResponseEntity.ok(reservationService.findReservation(reservationId));

    }

 

    @DeleteMapping(path = "", params= { "reservationId" })

    @ApiOperation(value="Cancel reservation by id", notes="Cancel reservation by id", nickname = "CancelReservationById")

    public ResponseEntity<ReservationDTO> cancelReservation(@PathVariable("reservationId") Long reservationId) {

        return ResponseEntity.ok(reservationService.cancelReservation(reservationId));

    }

 

   @PutMapping(path = "", params= { "reservationId", "scheduleId" })

    @ApiOperation(value="Reschedule reservation by reservationId and scheduleId", notes="Reschedule reservation by reservationId and scheduleId", nickname = "RescheduleReservation")

    public ResponseEntity<ReservationDTO> rescheduleReservation(@PathVariable("reservationId") Long reservationId, @PathVariable("scheduleId") Long scheduleId) {

        return ResponseEntity.ok(reservationService.rescheduleReservation(reservationId, scheduleId));

    }

}
