package handlers

import (
	"net/http"
	"strconv"

	"coworking-diploma/coworking-service/internal/models"
	"coworking-diploma/coworking-service/internal/services"

	"github.com/gin-gonic/gin"
)

type SeatHandler struct {
	Service *services.SeatService
}

func (h *SeatHandler) CreateSeat(c *gin.Context) {
	var seat models.Seat
	if err := c.ShouldBindJSON(&seat); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	if err := h.Service.CreateSeat(&seat); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create seat"})
		return
	}
	c.JSON(http.StatusCreated, gin.H{"message": "Seat created"})
}

func (h *SeatHandler) GetAllSeats(c *gin.Context) {
	seats, err := h.Service.GetAllSeats()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to retrieve seats"})
		return
	}
	c.JSON(http.StatusOK, seats)
}

func (h *SeatHandler) GetSeatByID(c *gin.Context) {
	seatID, _ := strconv.Atoi(c.Param("seat_id"))
	seat, err := h.Service.GetSeatByID(uint(seatID))
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Seat not found"})
		return
	}
	c.JSON(http.StatusOK, seat)
}

func (h *SeatHandler) UpdateSeat(c *gin.Context) {
	seatID, _ := strconv.Atoi(c.Param("seat_id"))
	var seat models.Seat
	if err := c.ShouldBindJSON(&seat); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	if err := h.Service.UpdateSeat(uint(seatID), &seat); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to update seat"})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Seat updated successfully"})
}

func (h *SeatHandler) DeleteSeat(c *gin.Context) {
	seatID, _ := strconv.Atoi(c.Param("seat_id"))
	if err := h.Service.DeleteSeat(uint(seatID)); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to delete seat"})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Seat deleted successfully"})
}

func (h *SeatHandler) GetSeatsByRoomID(c *gin.Context) {
	roomID, _ := strconv.Atoi(c.Param("room_id"))
	seats, err := h.Service.GetSeatsByRoomID(uint(roomID))
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to retrieve seats"})
		return
	}
	c.JSON(http.StatusOK, seats)
}
