package database

import (
	"fmt"
	"log"
	"time"

	"coworking-diploma/coworking-service/config"
	"coworking-diploma/coworking-service/internal/models"

	"gorm.io/driver/postgres"
	"gorm.io/gorm"
)

var DB *gorm.DB

func InitDB(cfg config.Config) {
	dsn := fmt.Sprintf("host=%s user=%s password=%s dbname=%s port=%s sslmode=disable",
		cfg.DBHost, cfg.DBUser, cfg.DBPassword, cfg.DBName, cfg.DBPort)

	var err error
	for i := 0; i < 10; i++ {
		DB, err = gorm.Open(postgres.Open(dsn), &gorm.Config{})
		if err == nil {
			break
		}
		log.Printf("Database not ready (attempt %d/10): %v", i+1, err)
		time.Sleep(2 * time.Second)
	}

	if err != nil {
		log.Fatalf("Could not connect to database: %v", err)
	}

	if err := DB.AutoMigrate(
		&models.CoworkingSpace{},
		&models.Room{},
		&models.Seat{},
	); err != nil {
		log.Fatalf("Failed to migrate: %v", err)
	}

	log.Println("Connected to database and migrated schema")
}
