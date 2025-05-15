package models

import "gorm.io/gorm"

type Room struct {
	gorm.Model
	SpaceID      uint    `json:"space_id" gorm:"not null;index"`
	Name         string  `json:"name" gorm:"not null"`
	Type         string  `json:"type" gorm:"not null"` // coworking or meeting room
	Capacity     int     `json:"capacity" gorm:"not null"`
	PricePerHour float64 `json:"price_per_hour" gorm:"not null"`
	Status       string  `json:"status" gorm:"not null"` // available or not
	Seats        []Seat  `gorm:"foreignKey:RoomID"`
}
