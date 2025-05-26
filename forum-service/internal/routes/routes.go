package routes

import (
	"coworking-diploma/forum-service/internal/handlers"

	"github.com/gin-gonic/gin"
)

func SetupRouter(postHandler *handlers.PostHandler, commentHandler *handlers.CommentHandler) *gin.Engine {
	r := gin.Default()

	api := r.Group("/api/forum")
	{
		// Комментарии: уникальный базовый путь
		api.POST("/posts/:post_id/comments", commentHandler.Create)
		api.GET("/posts/:post_id/comments", commentHandler.GetByPost)
		api.GET("/comments/:id", commentHandler.GetByID)
		api.DELETE("/comments/:id", commentHandler.Delete)
		api.GET("/comments/user/:user_id", commentHandler.GetByUser)

		// Посты: отдельный путь
		api.POST("/posts", postHandler.Create)
		api.GET("/posts", postHandler.GetAll)
		api.GET("/posts/user/:user_id", postHandler.GetByUser)
		api.GET("/posts/id/:id", postHandler.GetByID)   // <== меняем на id/:id!
		api.DELETE("/posts/id/:id", postHandler.Delete) // <== тоже!
	}

	return r
}
