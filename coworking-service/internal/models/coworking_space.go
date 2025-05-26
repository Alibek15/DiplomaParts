package models

import "gorm.io/gorm"

type CoworkingSpace struct {
	gorm.Model
	Name      string `json:"name" gorm:"not null"`
	Location  string `json:"location" gorm:"not null"`
	Amenities string `json:"amenities" gorm:"type:text"`
	Rooms     []Room `gorm:"foreignKey:SpaceID"`
}
