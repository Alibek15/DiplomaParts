package services

import (
	"coworking-diploma/forum-service/internal/models"
	"coworking-diploma/forum-service/internal/repositories"
	"time"
)

type CommentService interface {
	CreateComment(postID uint, userID int64, content string) error
	GetCommentsByPost(postID uint) ([]models.Comment, error)
	GetCommentByID(commentID uint) (*models.Comment, error)
	GetCommentsByUser(userID int64) ([]models.Comment, error)
	DeleteComment(commentID uint) error
}

type commentService struct {
	repo repositories.CommentRepository
}

func NewCommentService(repo repositories.CommentRepository) CommentService {
	return &commentService{repo: repo}
}

func (s *commentService) CreateComment(postID uint, userID int64, content string) error {
	comment := &models.Comment{
		PostID:    postID,
		UserID:    userID,
		Content:   content,
		CreatedAt: time.Now(),
	}
	return s.repo.Create(comment)
}

func (s *commentService) GetCommentsByPost(postID uint) ([]models.Comment, error) {
	return s.repo.GetByPost(postID)
}

func (s *commentService) GetCommentByID(commentID uint) (*models.Comment, error) {
	return s.repo.GetByID(commentID)
}

func (s *commentService) GetCommentsByUser(userID int64) ([]models.Comment, error) {
	return s.repo.GetByUser(userID)
}

func (s *commentService) DeleteComment(commentID uint) error {
	return s.repo.Delete(commentID)
}
