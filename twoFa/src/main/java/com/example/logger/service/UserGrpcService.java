package com.example.logger.service;

import com.example.logger.*;
import com.example.logger.entity.AccountStatus;
import com.example.logger.entity.Role;
import com.example.logger.entity.User;
import com.example.logger.repository.UserRepository;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@GrpcService
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepository userRepository;

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UpdateUserResponse> responseObserver) {
        Optional<User> optionalUser = userRepository.findById(request.getUserId());

        if (optionalUser.isEmpty()) {
            responseObserver.onNext(UpdateUserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("User not found")
                    .build());
            responseObserver.onCompleted();
            return;
        }

        User user = optionalUser.get();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        try {
            Role role = Role.valueOf(request.getRole().toUpperCase());

            if (role == Role.COMPANY) {
                user.setCompanyName(request.getCompanyName());
                user.setWebsite(request.getWebsite());
                user.setIndustry(request.getIndustry());
                user.setCompanySize(request.getCompanySize());
            } else {
                user.setCompanyName(null);
                user.setWebsite(null);
                user.setIndustry(null);
                user.setCompanySize(null);
            }

            userRepository.save(user);

            responseObserver.onNext(UpdateUserResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("User updated")
                    .build());
        } catch (IllegalArgumentException e) {
            responseObserver.onNext(UpdateUserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Invalid role: " + request.getRole())
                    .build());
        }

        responseObserver.onCompleted();
    }
    @Override
    public void deleteUser(DeleteUserRequest request, StreamObserver<DeleteUserResponse> responseObserver) {
        if (!request.getRequesterRole().equals("ADMIN")) {
            responseObserver.onNext(DeleteUserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Only ADMIN can delete users")
                    .build());
            responseObserver.onCompleted();
            return;
        }

        Optional<User> optionalUser = userRepository.findById(request.getUserId());
        if (optionalUser.isEmpty()) {
            responseObserver.onNext(DeleteUserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("User not found")
                    .build());
            responseObserver.onCompleted();
            return;
        }

        User user = optionalUser.get();
        user.setAccountStatus(AccountStatus.BANNED);
        userRepository.save(user);

        responseObserver.onNext(DeleteUserResponse.newBuilder()
                .setSuccess(true)
                .setMessage("User marked as BANNED")
                .build());
        responseObserver.onCompleted();
    }
}

