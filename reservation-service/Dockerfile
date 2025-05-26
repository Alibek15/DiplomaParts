FROM golang:1.23-alpine

WORKDIR /app

COPY reservation-service/go.mod reservation-service/go.sum ./

COPY proto ./proto

RUN go mod edit -replace=coworking-diploma/proto=./proto
RUN go mod tidy
RUN go mod download

COPY reservation-service/. .

RUN go build -o reservation-service cmd/main.go

EXPOSE 8082

CMD ["/app/reservation-service"]
