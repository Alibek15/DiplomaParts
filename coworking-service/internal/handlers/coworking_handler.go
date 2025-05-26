package handlers

import (
	"net/http"
	"strconv"

	"coworking-diploma/coworking-service/internal/models"
	"coworking-diploma/coworking-service/internal/services"

	"github.com/gin-gonic/gin"
)

type CoworkingHandler struct {
	Service *services.CoworkingService
}

func (h *CoworkingHandler) CreateSpace(c *gin.Context) {
	var space models.CoworkingSpace
	if err := c.ShouldBindJSON(&space); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	if err := h.Service.CreateSpace(&space); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create coworking space"})
		return
	}
	c.JSON(http.StatusCreated, gin.H{"message": "Coworking space created"})
}

func (h *CoworkingHandler) GetAllSpaces(c *gin.Context) {
	spaces, err := h.Service.GetAllSpaces()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to retrieve coworking spaces"})
		return
	}
	c.JSON(http.StatusOK, spaces)
}

func (h *CoworkingHandler) GetSpaceByID(c *gin.Context) {
	spaceID, _ := strconv.Atoi(c.Param("space_id"))
	space, err := h.Service.GetSpaceByID(uint(spaceID))
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Coworking space not found"})
		return
	}
	c.JSON(http.StatusOK, space)
}

func (h *CoworkingHandler) UpdateSpace(c *gin.Context) {
	spaceID, _ := strconv.Atoi(c.Param("space_id"))
	var space models.CoworkingSpace
	if err := c.ShouldBindJSON(&space); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	if err := h.Service.UpdateSpace(uint(spaceID), &space); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to update coworking space"})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Coworking space updated"})
}

func (h *CoworkingHandler) DeleteSpace(c *gin.Context) {
	spaceID, _ := strconv.Atoi(c.Param("space_id"))
	if err := h.Service.DeleteSpace(uint(spaceID)); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to delete coworking space"})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Coworking space deleted"})
}
