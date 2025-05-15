package repositories

import (
	"coworking-diploma/forum-service/internal/models"

	"gorm.io/gorm"
)

type PostRepository interface {
	Create(post *models.Post) error
	GetAll() ([]models.Post, error)
	GetByID(postID uint) (*models.Post, error)
	GetByUser(userID int64) ([]models.Post, error)
	Delete(postID uint) error
}

type postRepository struct {
	db *gorm.DB
}

func NewPostRepository(db *gorm.DB) PostRepository {
	return &postRepository{db: db}
}

func (r *postRepository) Create(post *models.Post) error {
	return r.db.Create(post).Error
}

func (r *postRepository) GetAll() ([]models.Post, error) {
	var posts []models.Post
	err := r.db.Order("created_at DESC").Find(&posts).Error
	return posts, err
}

func (r *postRepository) GetByID(postID uint) (*models.Post, error) {
	var post models.Post
	err := r.db.First(&post, postID).Error
	if err != nil {
		return nil, err
	}
	return &post, nil
}

func (r *postRepository) GetByUser(userID int64) ([]models.Post, error) {
	var posts []models.Post
	err := r.db.Where("user_id = ?", userID).Order("created_at DESC").Find(&posts).Error
	return posts, err
}

func (r *postRepository) Delete(postID uint) error {
	return r.db.Delete(&models.Post{}, postID).Error
}
