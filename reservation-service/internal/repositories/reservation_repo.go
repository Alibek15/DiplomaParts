package repositories

import (
	"github.com/t0rch13/reservation-service/internal/database"
	"github.com/t0rch13/reservation-service/internal/models"
)

type ReservationRepository struct{}

func (r *ReservationRepository) Create(reservation *models.Reservation) error {
	return database.DB.Create(reservation).Error
}

func (r *ReservationRepository) GetAll() ([]models.Reservation, error) {
	var reservations []models.Reservation
	err := database.DB.Find(&reservations).Error
	return reservations, err
}

func (r *ReservationRepository) GetByID(id uint) (*models.Reservation, error) {
	var reservation models.Reservation
	err := database.DB.First(&reservation, id).Error
	return &reservation, err
}

func (r *ReservationRepository) Update(reservation *models.Reservation) error {
	return database.DB.Save(reservation).Error
}

func (r *ReservationRepository) Delete(id uint) error {
	return database.DB.Delete(&models.Reservation{}, id).Error
}

func (r *ReservationRepository) HasConflict(reservation *models.Reservation) (bool, error) {
	var count int64
	query := database.DB.Model(&models.Reservation{}).
		Where("status = ?", "active").
		Where("start_time < ? AND end_time > ?", reservation.EndTime, reservation.StartTime)

	if reservation.RoomID != 0 {
		query = query.Where("room_id = ?", reservation.RoomID)
	} else if reservation.SeatID != 0 {
		query = query.Where("seat_id = ?", reservation.SeatID)
	}

	err := query.Count(&count).Error
	return count > 0, err
}
