package com.example.logger.service;

import com.example.logger.entity.AccountStatus;
import com.example.logger.entity.Role;
import com.example.logger.entity.User;
import com.example.logger.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import logger.UserServiceGrpc;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Optional;

@GrpcService
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepository userRepository;

    @Override
    public void updateUser(logger.User.UpdateUserRequest request,
                           StreamObserver<logger.User.UpdateUserResponse> responseObserver) {
        Optional<User> optionalUser = userRepository.findById(request.getUserId());

        if (optionalUser.isEmpty()) {
            responseObserver.onNext(logger.User.UpdateUserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("User not found")
                    .build());
            responseObserver.onCompleted();
            return;
        }

        User user = optionalUser.get();
        // Обновляем общие поля
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());


        try {
            Role role = Role.valueOf(request.getRole().name());
            user.setRole(role);

            if (role == Role.COMPANY) {
                user.setCompanyName(request.getCompanyName());
                user.setWebsite(request.getWebsite());
                user.setIndustry(request.getIndustry());
                user.setCompanySize(request.getCompanySize());

            } else {
                // Сбрасываем поля компании для других ролей
                user.setCompanyName(null);
                user.setWebsite(null);
                user.setIndustry(null);
                user.setCompanySize(null);

            }

            userRepository.save(user);

            responseObserver.onNext(logger.User.UpdateUserResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("User updated")
                    .build());
        } catch (IllegalArgumentException e) {
            responseObserver.onNext(logger.User.UpdateUserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Invalid role: " + request.getRole())
                    .build());
        }

        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(logger.User.DeleteUserRequest request,
                           StreamObserver<logger.User.DeleteUserResponse> responseObserver) {
        // Только ADMIN может «удалять» (менять статус на BANNED)
        if (!"ADMIN".equalsIgnoreCase(request.getRequesterRole())) {
            responseObserver.onNext(logger.User.DeleteUserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Only ADMIN can delete users")
                    .build());
            responseObserver.onCompleted();
            return;
        }

        Optional<User> optionalUser = userRepository.findById(request.getUserId());
        if (optionalUser.isEmpty()) {
            responseObserver.onNext(logger.User.DeleteUserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("User not found")
                    .build());
            responseObserver.onCompleted();
            return;
        }

        User user = optionalUser.get();
        user.setAccountStatus(AccountStatus.BANNED);
        userRepository.save(user);

        responseObserver.onNext(logger.User.DeleteUserResponse.newBuilder()
                .setSuccess(true)
                .setMessage("User marked as BANNED")
                .build());
        responseObserver.onCompleted();
    }
}