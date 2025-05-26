package routes

import (
	"github.com/t0rch13/reservation-service/internal/handlers"

	"github.com/gin-gonic/gin"
)

func SetupRoutes(router *gin.Engine, reservationHandler *handlers.ReservationHandler) {
	api := router.Group("/api/reservations")

	api.POST("/", reservationHandler.CreateReservation)
	api.GET("/", reservationHandler.GetAllReservations)
	api.GET("/:id", reservationHandler.GetReservationByID)
	api.PUT("/:id", reservationHandler.UpdateReservation)
	api.DELETE("/:id", reservationHandler.DeleteReservation)
}
