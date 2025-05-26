package services

import (
	"context"
	"fmt"

	"coworking-diploma/proto" // импорт protobuf

	"github.com/t0rch13/reservation-service/internal/grpcclient"
	"github.com/t0rch13/reservation-service/internal/models"
	"github.com/t0rch13/reservation-service/internal/repositories"
)

type ReservationService struct {
	Repo            *repositories.ReservationRepository
	CoworkingClient grpcclient.CoworkingServiceClient
}

func (s *ReservationService) CreateReservation(reservation *models.Reservation) error {
	ctx := context.Background()

	if reservation.RoomID != 0 {
		_, err := s.CoworkingClient.GetRoomByID(ctx, &proto.GetRoomRequest{
			RoomId: uint32(reservation.RoomID),
		})
		if err != nil {
			return fmt.Errorf("room not found: %w", err)
		}
	} else if reservation.SeatID != 0 {
		_, err := s.CoworkingClient.GetSeatByID(ctx, &proto.GetSeatRequest{
			SeatId: uint32(reservation.SeatID),
		})
		if err != nil {
			return fmt.Errorf("seat not found: %w", err)
		}
	} else {
		return fmt.Errorf("either RoomID or SeatID must be provided")
	}

	conflict, err := s.Repo.HasConflict(reservation) // Check for time conflicts in the database
	if err != nil {
		return fmt.Errorf("conflict check failed: %w", err)
	}
	if conflict {
		return fmt.Errorf("room or seat already reserved during this time")
	}

	return s.Repo.Create(reservation)
}

func (s *ReservationService) GetAllReservations() ([]models.Reservation, error) {
	return s.Repo.GetAll()
}

func (s *ReservationService) GetReservationByID(id uint) (*models.Reservation, error) {
	return s.Repo.GetByID(id)
}

func (s *ReservationService) UpdateReservation(id uint, updated *models.Reservation) error {
	existing, err := s.Repo.GetByID(id)
	if err != nil {
		return err
	}

	existing.UserID = updated.UserID
	existing.RoomID = updated.RoomID
	existing.SeatID = updated.SeatID
	existing.StartTime = updated.StartTime
	existing.EndTime = updated.EndTime
	existing.TotalPrice = updated.TotalPrice
	existing.Status = updated.Status

	return s.Repo.Update(existing)
}

func (s *ReservationService) DeleteReservation(id uint) error {
	res, err := s.Repo.GetByID(id)
	if err != nil {
		return err
	}

	res.Status = "canceled"
	if err := s.Repo.Update(res); err != nil {
		return err
	}
	return s.Repo.Delete(id)
}
