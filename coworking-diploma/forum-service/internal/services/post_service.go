package services

import (
	"coworking-diploma/forum-service/internal/models"
	"coworking-diploma/forum-service/internal/repositories"
	"time"
)

type PostService interface {
	CreatePost(userID int64, title, content string, tags []string) error
	GetAllPosts() ([]models.Post, error)
	GetPostByID(postID uint) (*models.Post, error)
	GetPostsByUser(userID int64) ([]models.Post, error)
	DeletePost(postID uint) error
}

type postService struct {
	repo repositories.PostRepository
}

func NewPostService(repo repositories.PostRepository) PostService {
	return &postService{repo: repo}
}

func (s *postService) CreatePost(userID int64, title, content string, tags []string) error {
	post := &models.Post{
		UserID:    userID,
		Title:     title,
		Content:   content,
		Tags:      tags,
		CreatedAt: time.Now(),
	}
	return s.repo.Create(post)
}

func (s *postService) GetAllPosts() ([]models.Post, error) {
	return s.repo.GetAll()
}

func (s *postService) GetPostByID(postID uint) (*models.Post, error) {
	return s.repo.GetByID(postID)
}

func (s *postService) GetPostsByUser(userID int64) ([]models.Post, error) {
	return s.repo.GetByUser(userID)
}

func (s *postService) DeletePost(postID uint) error {
	return s.repo.Delete(postID)
}
