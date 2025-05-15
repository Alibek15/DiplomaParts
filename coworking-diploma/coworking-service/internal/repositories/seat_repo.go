package repositories

import (
	"coworking-diploma/coworking-service/internal/database"
	"coworking-diploma/coworking-service/internal/models"
)

type SeatRepository struct{}

func (r *SeatRepository) Create(seat *models.Seat) error {
	return database.DB.Create(seat).Error
}

func (r *SeatRepository) GetAll() ([]models.Seat, error) {
	var seats []models.Seat
	err := database.DB.Find(&seats).Error
	return seats, err
}

func (r *SeatRepository) GetByID(id uint) (*models.Seat, error) {
	var seat models.Seat
	err := database.DB.First(&seat, id).Error
	return &seat, err
}

func (r *SeatRepository) Update(seat *models.Seat) error {
	return database.DB.Save(seat).Error
}

func (r *SeatRepository) Delete(id uint) error {
	return database.DB.Delete(&models.Seat{}, id).Error
}

func (r *SeatRepository) GetSeatsByRoomID(roomID uint) ([]models.Seat, error) {
	var seats []models.Seat
	err := database.DB.Where("room_id = ?", roomID).Find(&seats).Error
	return seats, err
}
