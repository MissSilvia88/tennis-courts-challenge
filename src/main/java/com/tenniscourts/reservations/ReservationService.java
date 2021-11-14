ReservationService.java

package com.tenniscourts.reservations;

 

import com.tenniscourts.exceptions.AlreadyExistsEntityException;

import com.tenniscourts.exceptions.EntityNotFoundException;

import com.tenniscourts.guests.GuestRepository;

import com.tenniscourts.schedules.ScheduleRepository;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

 

import java.math.BigDecimal;

import java.time.LocalDateTime;

import java.time.temporal.ChronoUnit;

 

@Service

@AllArgsConstructor

public class ReservationService {

 

    private final ReservationRepository reservationRepository;

 

    private final ReservationMapper reservationMapper;

 

    private final GuestRepository guestRepository;

 

    private final ScheduleRepository scheduleRepository;

 

    // 1. As a user i want to be able to book a reservation for one or more tennis courts at a given date schedule

    public ReservationDTO bookReservation(CreateReservationRequestDTO createReservationRequestDTO) {

        boolean exists = reservationRepository.findBySchedule_Id(createReservationRequestDTO.getScheduleId()).stream().anyMatch(res ->

                ReservationStatus.READY_TO_PLAY.equals(res.getReservationStatus()));

 

        if(exists){

            throw new AlreadyExistsEntityException("Reservation already exists");

        }

 

        return guestRepository.findById(createReservationRequestDTO.getGuestId()).map(guest ->

                scheduleRepository.findById(createReservationRequestDTO.getScheduleId()).map(schedule -> {

                  Reservation reservation = reservationMapper.map(createReservationRequestDTO);

                  reservation.setGuest(guest);

                  reservation.setReservationStatus(ReservationStatus.READY_TO_PLAY);

                  reservation.setValue(BigDecimal.valueOf(20));

                  schedule.addReservation(reservation);

                  return reservationMapper.map(reservationRepository.saveAndFlush(reservation));

                })

                .orElseThrow(() -> {

                    throw new EntityNotFoundException("Schedule does not exist");

                }))

                .orElseThrow(() -> {

                   throw new EntityNotFoundException("Guest does not exist");

                });

    }

 

    public ReservationDTO findReservation(Long reservationId) {

        return reservationRepository.findById(reservationId).map(reservationMapper::map).orElseThrow(() -> {

            throw new EntityNotFoundException("Reservation not found.");

        });

    }

 

    //3. As a user I want to be able to cancel a reservation

    public ReservationDTO cancelReservation(Long reservationId) {

        return reservationMapper.map(this.cancel(reservationId));

    }

 

    private Reservation cancel(Long reservationId) {

        return reservationRepository.findById(reservationId).map(reservation -> {

 

            this.validateCancellation(reservation);

 

            BigDecimal refundValue = getRefundValue(reservation);

            return this.updateReservation(reservation, refundValue, ReservationStatus.CANCELLED);

 

        }).orElseThrow(() -> {

            throw new EntityNotFoundException("Reservation not found.");

        });

    }

 

    private Reservation updateReservation(Reservation reservation, BigDecimal refundValue, ReservationStatus status) {

        reservation.setReservationStatus(status);

        reservation.setValue(reservation.getValue().subtract(refundValue));

        reservation.setRefundValue(refundValue);

 

        return reservationRepository.save(reservation);

    }

 

    private void validateCancellation(Reservation reservation) {

        if (!ReservationStatus.READY_TO_PLAY.equals(reservation.getReservationStatus())) {

            throw new IllegalArgumentException("Cannot cancel/reschedule because it's not in ready to play status.");

        }

 

        if (reservation.getSchedule().getStartDateTime().isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException("Can cancel/reschedule only future dates.");

        }

    }

 

    public BigDecimal getRefundValue(Reservation reservation) {

//        long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), reservation.getSchedule().getStartDateTime());

        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), reservation.getSchedule().getStartDateTime());

 

        if (minutes >= 1440) {

            return reservation.getValue();

        } else if (minutes >= 720) {

            return reservation.getValue().multiply(new BigDecimal("0.75"));

        } else if (minutes >= 120) {

            return reservation.getValue().multiply(new BigDecimal("0.50"));

        } else if (minutes >= 1) {

            return reservation.getValue().multiply(new BigDecimal("0.25"));

        }

 

        return BigDecimal.ZERO;

    }

 

    /*TODO: This method actually not fully working, find a way to fix the issue when it's throwing the error:

            "Cannot reschedule to the same slot.*/

 

    //4. As a User I want to be able to reschedule a reservation

    public ReservationDTO rescheduleReservation(Long previousReservationId, Long scheduleId) {

        Long previousScheduleId = findReservation(previousReservationId).getSchedule().getId();

        if(scheduleId.equals(previousScheduleId)) {

            throw new IllegalArgumentException("Cannot reschedule to the same slot.");

        }

        Reservation previousReservation = cancel(previousReservationId);

        previousReservation.setReservationStatus(ReservationStatus.RESCHEDULED);

        reservationRepository.save(previousReservation);

 

        ReservationDTO newReservation = bookReservation(CreateReservationRequestDTO.builder()

                .guestId(previousReservation.getGuest().getId())

                .scheduleId(scheduleId)

                .build());

        newReservation.setPreviousReservation(reservationMapper.map(previousReservation));

        return newReservation;

    }

}
