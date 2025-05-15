package main

import (
	"github.com/gin-gonic/gin"
	"github.com/t0rch13/reservation-service/config"
	"github.com/t0rch13/reservation-service/internal/database"
	grpcclient "github.com/t0rch13/reservation-service/internal/grpcclient"
	"github.com/t0rch13/reservation-service/internal/handlers"
	"github.com/t0rch13/reservation-service/internal/repositories"
	"github.com/t0rch13/reservation-service/internal/routes"
	"github.com/t0rch13/reservation-service/internal/services"
)

func main() {
	cfg := config.LoadConfig()
	database.InitDB(cfg)

	coworkingClient := grpcclient.NewCoworkingClient()

	reservationRepo := repositories.ReservationRepository{}
	reservationService := services.ReservationService{
		Repo:            &reservationRepo,
		CoworkingClient: coworkingClient,
	}

	reservationHandler := handlers.NewReservationHandler(&reservationService, coworkingClient)

	r := gin.Default()
	routes.SetupRoutes(r, reservationHandler)

	r.Run(":8082")
}
