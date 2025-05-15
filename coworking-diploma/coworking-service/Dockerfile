FROM golang:1.23-alpine

WORKDIR /app

COPY coworking-service/go.mod coworking-service/go.sum ./
COPY proto ./proto

RUN go mod edit -replace=coworking-diploma/proto=./proto
RUN go mod tidy
RUN go mod download

COPY coworking-service/. .

RUN go build -o coworking-service cmd/main.go

EXPOSE 8081
EXPOSE 50051

CMD ["/app/coworking-service"]
