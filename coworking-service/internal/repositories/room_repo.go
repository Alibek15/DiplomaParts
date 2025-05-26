package repositories

import (
	"coworking-diploma/coworking-service/internal/database"
	"coworking-diploma/coworking-service/internal/models"
)

type RoomRepository struct{}

func (r *RoomRepository) Create(room *models.Room) error {
	return database.DB.Create(room).Error
}

func (r *RoomRepository) GetAll() ([]models.Room, error) {
	var rooms []models.Room
	err := database.DB.Preload("Seats").Find(&rooms).Error
	return rooms, err
}

func (r *RoomRepository) GetByID(id uint) (*models.Room, error) {
	var room models.Room
	err := database.DB.Preload("Seats").First(&room, id).Error
	return &room, err
}

func (r *RoomRepository) Update(room *models.Room) error {
	return database.DB.Save(room).Error
}

func (r *RoomRepository) Delete(id uint) error {
	return database.DB.Delete(&models.Room{}, id).Error
}

func (r *RoomRepository) GetRoomsBySpaceID(spaceID uint) ([]models.Room, error) {
	var rooms []models.Room
	err := database.DB.Where("space_id = ?", spaceID).Find(&rooms).Error
	return rooms, err
}
