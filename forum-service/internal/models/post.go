package models

import (
	"time"

	"github.com/lib/pq"
)

type Post struct {
	ID        uint           `gorm:"primaryKey"`
	UserID    int64          `gorm:"not null"`
	Title     string         `gorm:"not null"`
	Content   string         `gorm:"not null"`
	Tags      pq.StringArray `gorm:"type:text[]" json:"tags"`
	CreatedAt time.Time      `gorm:"autoCreateTime"`
}
