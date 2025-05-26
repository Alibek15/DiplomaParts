package services

import (
	"coworking-diploma/coworking-service/internal/models"
	"coworking-diploma/coworking-service/internal/repositories"
)

type CoworkingService struct {
	Repo *repositories.CoworkingRepository
}

func (s *CoworkingService) CreateSpace(space *models.CoworkingSpace) error {
	return s.Repo.Create(space)
}

func (s *CoworkingService) GetAllSpaces() ([]models.CoworkingSpace, error) {
	return s.Repo.GetAll()
}

func (s *CoworkingService) GetSpaceByID(id uint) (*models.CoworkingSpace, error) {
	return s.Repo.GetByID(id)
}

func (s *CoworkingService) UpdateSpace(id uint, space *models.CoworkingSpace) error {
	existingSpace, err := s.Repo.GetByID(id)
	if err != nil {
		return err
	}

	existingSpace.Name = space.Name
	existingSpace.Location = space.Location
	existingSpace.Amenities = space.Amenities

	return s.Repo.Update(existingSpace)
}

func (s *CoworkingService) DeleteSpace(id uint) error {
	return s.Repo.Delete(id)
}
