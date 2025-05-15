package handlers

import (
	"net/http"
	"strconv"
	"time"

	"github.com/t0rch13/reservation-service/internal/grpcclient"
	"github.com/t0rch13/reservation-service/internal/models"
	"github.com/t0rch13/reservation-service/internal/services"

	coworkingpb "coworking-diploma/proto"

	"github.com/gin-gonic/gin"
)

type ReservationHandler struct {
	Service         *services.ReservationService
	CoworkingClient grpcclient.CoworkingServiceClient
}

func NewReservationHandler(service *services.ReservationService, coworkingClient grpcclient.CoworkingServiceClient) *ReservationHandler {
	return &ReservationHandler{
		Service:         service,
		CoworkingClient: coworkingClient,
	}
}

func (h *ReservationHandler) CreateReservation(c *gin.Context) {
	var reservation models.Reservation

	if err := c.ShouldBindJSON(&reservation); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	layout := time.RFC3339 // date and time format

	startTime, err := time.Parse(layout, reservation.StartTime)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid start time format"})
		return
	}

	if startTime.Before(time.Now()) {
		c.JSON(http.StatusBadRequest, gin.H{"error": "cannot create reservation in the past"})
		return
	}

	if (reservation.RoomID == 0 && reservation.SeatID == 0) || (reservation.RoomID != 0 && reservation.SeatID != 0) {
		c.JSON(http.StatusBadRequest, gin.H{"error": "provide either RoomID or SeatID (not both or neither)"})
		return
	}

	if reservation.RoomID != 0 {
		roomResp, err := h.CoworkingClient.GetRoomByID(c.Request.Context(), &coworkingpb.GetRoomRequest{
			RoomId: uint32(reservation.RoomID),
		})
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to get room info: " + err.Error()})
			return
		}
		if roomResp.Status != "available" {
			c.JSON(http.StatusConflict, gin.H{"error": "room is not available"})
			return
		}
	} else if reservation.SeatID != 0 {
		seatResp, err := h.CoworkingClient.GetSeatByID(c.Request.Context(), &coworkingpb.GetSeatRequest{
			SeatId: uint32(reservation.SeatID),
		})
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to get seat info: " + err.Error()})
			return
		}
		if seatResp.Status != "available" {
			c.JSON(http.StatusConflict, gin.H{"error": "seat is not available"})
			return
		}
	}

	if err := h.Service.CreateReservation(&reservation); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to create reservation: " + err.Error()})
		return
	}

	c.JSON(http.StatusCreated, gin.H{"message": "reservation created successfully"})
}

func (h *ReservationHandler) GetAllReservations(c *gin.Context) {
	reservations, err := h.Service.GetAllReservations()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to retrieve reservations"})
		return
	}

	c.JSON(http.StatusOK, reservations)
}

func (h *ReservationHandler) GetReservationByID(c *gin.Context) {
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid ID"})
		return
	}

	reservation, err := h.Service.GetReservationByID(uint(id))
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "reservation not found"})
		return
	}

	c.JSON(http.StatusOK, reservation)
}

func (h *ReservationHandler) UpdateReservation(c *gin.Context) {
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid ID"})
		return
	}

	var reservation models.Reservation
	if err := c.ShouldBindJSON(&reservation); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if err := h.Service.UpdateReservation(uint(id), &reservation); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to update reservation"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "reservation updated successfully"})
}

func (h *ReservationHandler) DeleteReservation(c *gin.Context) {
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid ID"})
		return
	}

	if err := h.Service.DeleteReservation(uint(id)); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to delete reservation"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "reservation deleted successfully"})
}
