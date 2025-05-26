package main

import (
	"coworking-diploma/coworking-service/config"
	"coworking-diploma/coworking-service/internal/database"
	"coworking-diploma/coworking-service/internal/handlers"
	"coworking-diploma/coworking-service/internal/repositories"
	"coworking-diploma/coworking-service/internal/routes"
	"coworking-diploma/coworking-service/internal/services"
	"log"

	grpcserver "coworking-diploma/coworking-service/internal/grpc"

	"github.com/gin-gonic/gin"
)

func main() {
	cfg := config.LoadConfig()
	database.InitDB(cfg)

	spaceRepo := repositories.CoworkingRepository{}
	spaceService := services.CoworkingService{Repo: &spaceRepo}
	spaceHandler := handlers.CoworkingHandler{Service: &spaceService}

	roomRepo := repositories.RoomRepository{}
	roomService := services.RoomService{Repo: &roomRepo}
	roomHandler := handlers.RoomHandler{Service: &roomService}

	seatRepo := repositories.SeatRepository{}
	seatService := services.SeatService{Repo: &seatRepo}
	seatHandler := handlers.SeatHandler{Service: &seatService}

	go func() {
		grpcServer := grpcserver.NewServer(&roomRepo, &seatRepo)
		if err := grpcServer.Start(); err != nil {
			log.Fatalf("failed to start gRPC server: %v", err)
		}
	}()

	r := gin.Default()
	routes.SetupRoutes(r, &spaceHandler, &roomHandler, &seatHandler)

	r.Run(":8081")
}
