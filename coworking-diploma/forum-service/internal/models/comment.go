package models

import (
	"time"
)

type Comment struct {
	ID        uint      `gorm:"primaryKey"`
	PostID    uint      `gorm:"not null;index"`
	UserID    int64     `gorm:"not null"`
	Content   string    `gorm:"not null"`
	CreatedAt time.Time `gorm:"autoCreateTime"`
}
