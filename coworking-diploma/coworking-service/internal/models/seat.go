package models

import "gorm.io/gorm"

type Seat struct {
	gorm.Model
	RoomID       uint    `json:"room_id" gorm:"not null;index"`
	Number       string  `json:"number" gorm:"not null"`
	PricePerHour float64 `json:"price_per_hour" gorm:"not null"`
	Status       string  `json:"status" gorm:"not null"`
}
