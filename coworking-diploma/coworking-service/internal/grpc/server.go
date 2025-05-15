package grpcserver

import (
	"context"
	"fmt"
	"log"
	"net"

	"coworking-diploma/coworking-service/internal/repositories"
	coworkingpb "coworking-diploma/proto"

	"google.golang.org/grpc"
	"google.golang.org/grpc/reflection"
)

const GRPC_PORT = ":50051"

type Server struct {
	coworkingpb.UnimplementedCoworkingServiceServer
	roomRepo *repositories.RoomRepository
	seatRepo *repositories.SeatRepository
}

func NewServer(roomRepo *repositories.RoomRepository, seatRepo *repositories.SeatRepository) *Server {
	return &Server{roomRepo: roomRepo, seatRepo: seatRepo}
}

func (s *Server) Start() error {
	lis, err := net.Listen("tcp", GRPC_PORT)
	if err != nil {
		return fmt.Errorf("failed to listen: %w", err)
	}

	grpcServer := grpc.NewServer()
	coworkingpb.RegisterCoworkingServiceServer(grpcServer, s)

	// grpc reflection for testing
	reflection.Register(grpcServer)

	log.Printf("gRPC server will listen on %s", GRPC_PORT)

	return grpcServer.Serve(lis)
}

func (s *Server) GetRoomByID(ctx context.Context, req *coworkingpb.GetRoomRequest) (*coworkingpb.GetRoomResponse, error) {
	room, err := s.roomRepo.GetByID(uint(req.RoomId))
	if err != nil {
		return nil, err
	}

	return &coworkingpb.GetRoomResponse{
		Id:           uint32(room.ID),
		Name:         room.Name,
		Type:         room.Type,
		Capacity:     int32(room.Capacity),
		PricePerHour: room.PricePerHour,
		Status:       room.Status,
	}, nil
}

func (s *Server) GetSeatByID(ctx context.Context, req *coworkingpb.GetSeatRequest) (*coworkingpb.GetSeatResponse, error) {
	seat, err := s.seatRepo.GetByID(uint(req.SeatId))
	if err != nil {
		return nil, err
	}

	return &coworkingpb.GetSeatResponse{
		Id:           uint32(seat.ID),
		Number:       seat.Number,
		PricePerHour: seat.PricePerHour,
		Status:       seat.Status,
	}, nil
}
