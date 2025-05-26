package repositories

import (
	"coworking-diploma/coworking-service/internal/database"
	"coworking-diploma/coworking-service/internal/models"
)

type CoworkingRepository struct{}

func (r *CoworkingRepository) Create(space *models.CoworkingSpace) error {
	return database.DB.Create(space).Error
}

func (r *CoworkingRepository) GetAll() ([]models.CoworkingSpace, error) {
	var spaces []models.CoworkingSpace
	err := database.DB.Preload("Rooms.Seats").Find(&spaces).Error
	return spaces, err
}

func (r *CoworkingRepository) GetByID(id uint) (*models.CoworkingSpace, error) {
	var space models.CoworkingSpace
	err := database.DB.Preload("Rooms.Seats").First(&space, id).Error
	return &space, err
}

func (r *CoworkingRepository) Update(space *models.CoworkingSpace) error {
	return database.DB.Save(space).Error
}

func (r *CoworkingRepository) Delete(id uint) error {
	return database.DB.Delete(&models.CoworkingSpace{}, id).Error
}
