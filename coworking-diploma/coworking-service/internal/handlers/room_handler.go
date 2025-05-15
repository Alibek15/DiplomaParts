package handlers

import (
	"net/http"
	"strconv"

	"coworking-diploma/coworking-service/internal/models"
	"coworking-diploma/coworking-service/internal/services"

	"github.com/gin-gonic/gin"
)

type RoomHandler struct {
	Service *services.RoomService
}

func (h *RoomHandler) CreateRoom(c *gin.Context) {
	var room models.Room
	if err := c.ShouldBindJSON(&room); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	if err := h.Service.CreateRoom(&room); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create room"})
		return
	}
	c.JSON(http.StatusCreated, gin.H{"message": "Room created"})
}

func (h *RoomHandler) GetAllRooms(c *gin.Context) {
	rooms, err := h.Service.GetAllRooms()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to retrieve rooms"})
		return
	}
	c.JSON(http.StatusOK, rooms)
}

func (h *RoomHandler) GetRoomByID(c *gin.Context) {
	roomID, _ := strconv.Atoi(c.Param("room_id"))
	room, err := h.Service.GetRoomByID(uint(roomID))
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Room not found"})
		return
	}
	c.JSON(http.StatusOK, room)
}

func (h *RoomHandler) UpdateRoom(c *gin.Context) {
	roomID, _ := strconv.Atoi(c.Param("room_id"))
	var room models.Room
	if err := c.ShouldBindJSON(&room); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	if err := h.Service.UpdateRoom(uint(roomID), &room); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to update room"})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Room updated successfully"})
}

func (h *RoomHandler) DeleteRoom(c *gin.Context) {
	roomID, _ := strconv.Atoi(c.Param("room_id"))
	if err := h.Service.DeleteRoom(uint(roomID)); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to delete room"})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Room deleted successfully"})
}

func (h *RoomHandler) GetRoomsBySpaceID(c *gin.Context) {
	spaceID, _ := strconv.Atoi(c.Param("space_id"))
	rooms, err := h.Service.GetRoomsBySpaceID(uint(spaceID))
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to retrieve rooms"})
		return
	}
	c.JSON(http.StatusOK, rooms)
}
