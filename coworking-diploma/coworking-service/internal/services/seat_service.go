package services

import (
	"coworking-diploma/coworking-service/internal/models"
	"coworking-diploma/coworking-service/internal/repositories"
)

type SeatService struct {
	Repo *repositories.SeatRepository
}

func (s *SeatService) CreateSeat(seat *models.Seat) error {
	return s.Repo.Create(seat)
}

func (s *SeatService) GetAllSeats() ([]models.Seat, error) {
	return s.Repo.GetAll()
}

func (s *SeatService) GetSeatByID(id uint) (*models.Seat, error) {
	return s.Repo.GetByID(id)
}

func (s *SeatService) UpdateSeat(id uint, seat *models.Seat) error {
	existingSeat, err := s.Repo.GetByID(id)
	if err != nil {
		return err
	}

	existingSeat.Number = seat.Number
	existingSeat.PricePerHour = seat.PricePerHour
	existingSeat.Status = seat.Status

	return s.Repo.Update(existingSeat)
}

func (s *SeatService) DeleteSeat(id uint) error {
	return s.Repo.Delete(id)
}

func (s *SeatService) GetSeatsByRoomID(roomID uint) ([]models.Seat, error) {
	return s.Repo.GetSeatsByRoomID(roomID)
}
