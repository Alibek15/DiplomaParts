package main

import (
	"log"

	"coworking-diploma/forum-service/config"
	"coworking-diploma/forum-service/internal/database"
	"coworking-diploma/forum-service/internal/handlers"
	"coworking-diploma/forum-service/internal/repositories"
	"coworking-diploma/forum-service/internal/routes"
	"coworking-diploma/forum-service/internal/services"
)

func main() {
	cfg := config.LoadConfig()
	db := database.InitDB(cfg)

	postRepo := repositories.NewPostRepository(db)
	postService := services.NewPostService(postRepo)
	postHandler := handlers.NewPostHandler(postService)

	commentRepo := repositories.NewCommentRepository(db)
	commentService := services.NewCommentService(commentRepo)
	commentHandler := handlers.NewCommentHandler(commentService)

	router := routes.SetupRouter(postHandler, commentHandler)

	if err := router.Run(":" + cfg.ServerPort); err != nil {
		log.Fatal("Server failed to start:", err)
	}
}
