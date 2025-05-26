package grpcclient

import (
	"context"
	"log"
	"time"

	coworkingpb "coworking-diploma/proto"

	"google.golang.org/grpc"
)

const coworkingServiceAddress = "coworking_service:50051"

type CoworkingServiceClient interface {
	GetRoomByID(ctx context.Context, req *coworkingpb.GetRoomRequest) (*coworkingpb.GetRoomResponse, error)
	GetSeatByID(ctx context.Context, req *coworkingpb.GetSeatRequest) (*coworkingpb.GetSeatResponse, error)
}

type CoworkingClient struct {
	conn   *grpc.ClientConn
	client coworkingpb.CoworkingServiceClient
}

func NewCoworkingClient() *CoworkingClient {
	conn, err := grpc.Dial(coworkingServiceAddress, grpc.WithInsecure(), grpc.WithBlock(), grpc.WithTimeout(5*time.Second))
	if err != nil {
		log.Fatalf("failed to connect to coworking service: %v", err)
	}

	client := coworkingpb.NewCoworkingServiceClient(conn)

	return &CoworkingClient{
		conn:   conn,
		client: client,
	}
}

func (c *CoworkingClient) GetRoomByID(ctx context.Context, req *coworkingpb.GetRoomRequest) (*coworkingpb.GetRoomResponse, error) {
	return c.client.GetRoomByID(ctx, req)
}

func (c *CoworkingClient) GetSeatByID(ctx context.Context, req *coworkingpb.GetSeatRequest) (*coworkingpb.GetSeatResponse, error) {
	return c.client.GetSeatByID(ctx, req)
}
