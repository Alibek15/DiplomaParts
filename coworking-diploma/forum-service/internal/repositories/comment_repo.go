package repositories

import (
	"coworking-diploma/forum-service/internal/models"

	"gorm.io/gorm"
)

type CommentRepository interface {
	Create(comment *models.Comment) error
	GetByPost(postID uint) ([]models.Comment, error)
	GetByID(commentID uint) (*models.Comment, error)
	GetByUser(userID int64) ([]models.Comment, error)
	Delete(commentID uint) error
}

type commentRepository struct {
	db *gorm.DB
}

func NewCommentRepository(db *gorm.DB) CommentRepository {
	return &commentRepository{db: db}
}

func (r *commentRepository) Create(comment *models.Comment) error {
	return r.db.Create(comment).Error
}

func (r *commentRepository) GetByPost(postID uint) ([]models.Comment, error) {
	var comments []models.Comment
	err := r.db.Where("post_id = ?", postID).Order("created_at ASC").Find(&comments).Error
	return comments, err
}

func (r *commentRepository) GetByID(commentID uint) (*models.Comment, error) {
	var comment models.Comment
	err := r.db.First(&comment, commentID).Error
	if err != nil {
		return nil, err
	}
	return &comment, nil
}

func (r *commentRepository) GetByUser(userID int64) ([]models.Comment, error) {
	var comments []models.Comment
	err := r.db.Where("user_id = ?", userID).Order("created_at ASC").Find(&comments).Error
	return comments, err
}

func (r *commentRepository) Delete(commentID uint) error {
	return r.db.Delete(&models.Comment{}, commentID).Error
}
