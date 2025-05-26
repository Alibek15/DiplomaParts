package routes

import (
	"coworking-diploma/coworking-service/internal/handlers"

	"github.com/gin-gonic/gin"
)

func SetupRoutes(router *gin.Engine, coworkingHandler *handlers.CoworkingHandler, roomHandler *handlers.RoomHandler, seatHandler *handlers.SeatHandler) {
	api := router.Group("/api/coworking")

	// Coworking Spaces
	api.POST("/spaces", coworkingHandler.CreateSpace)
	api.GET("/spaces", coworkingHandler.GetAllSpaces)
	api.GET("/spaces/details/:space_id", coworkingHandler.GetSpaceByID)
	api.PUT("/spaces/:space_id", coworkingHandler.UpdateSpace)
	api.DELETE("/spaces/:space_id", coworkingHandler.DeleteSpace)

	// Rooms
	api.POST("/rooms", roomHandler.CreateRoom)
	api.GET("/rooms", roomHandler.GetAllRooms)
	api.GET("/rooms/details/:room_id", roomHandler.GetRoomByID)
	api.PUT("/rooms/:room_id", roomHandler.UpdateRoom)
	api.DELETE("/rooms/:room_id", roomHandler.DeleteRoom)
	api.GET("/spaces/:space_id/rooms", roomHandler.GetRoomsBySpaceID)

	// Seats
	api.POST("/seats", seatHandler.CreateSeat)
	api.GET("/seats", seatHandler.GetAllSeats)
	api.GET("/seats/details/:seat_id", seatHandler.GetSeatByID)
	api.PUT("/seats/:seat_id", seatHandler.UpdateSeat)
	api.DELETE("/seats/:seat_id", seatHandler.DeleteSeat)
	api.GET("/rooms/:room_id/seats", seatHandler.GetSeatsByRoomID)
}
