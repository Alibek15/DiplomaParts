package models

import (
	"gorm.io/gorm"
)

type Reservation struct {
	gorm.Model
	UserID     uint    `json:"user_id" gorm:"not null;index"`
	RoomID     uint    `json:"room_id" gorm:"index"`
	SeatID     uint    `json:"seat_id" gorm:"index"`
	StartTime  string  `json:"start_time" gorm:"not null"`
	EndTime    string  `json:"end_time" gorm:"not null"`
	TotalPrice float64 `json:"total_price" gorm:"not null"`
	Status     string  `json:"status" gorm:"not null"`
}
