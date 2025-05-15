package services

import (
	"coworking-diploma/coworking-service/internal/models"
	"coworking-diploma/coworking-service/internal/repositories"
)

type RoomService struct {
	Repo *repositories.RoomRepository
}

func (s *RoomService) CreateRoom(room *models.Room) error {
	return s.Repo.Create(room)
}

func (s *RoomService) GetAllRooms() ([]models.Room, error) {
	return s.Repo.GetAll()
}

func (s *RoomService) GetRoomByID(id uint) (*models.Room, error) {
	return s.Repo.GetByID(id)
}

func (s *RoomService) UpdateRoom(id uint, room *models.Room) error {
	existingRoom, err := s.Repo.GetByID(id)
	if err != nil {
		return err
	}

	existingRoom.Name = room.Name
	existingRoom.Type = room.Type
	existingRoom.Capacity = room.Capacity
	existingRoom.PricePerHour = room.PricePerHour
	existingRoom.Status = room.Status

	return s.Repo.Update(existingRoom)
}

func (s *RoomService) DeleteRoom(id uint) error {
	return s.Repo.Delete(id)
}

func (s *RoomService) GetRoomsBySpaceID(spaceID uint) ([]models.Room, error) {
	return s.Repo.GetRoomsBySpaceID(spaceID)
}
