package com.safalifter.userservice.service;

import com.safalifter.userservice.client.FileStorageClient;
import com.safalifter.userservice.command.CreateUserCommand;
import com.safalifter.userservice.command.DeleteUserCommand;
import com.safalifter.userservice.command.UpdateUserCommand;
import com.safalifter.userservice.enums.Active;
import com.safalifter.userservice.enums.Role;
import com.safalifter.userservice.model.User;
import com.safalifter.userservice.model.UserDetails;
import com.safalifter.userservice.query.FindAllUsersQuery;
import com.safalifter.userservice.query.FindUserByEmailQuery;
import com.safalifter.userservice.query.FindUserByIdQuery;
import com.safalifter.userservice.query.FindUserByUsernameQuery;
import com.safalifter.userservice.request.RegisterRequest;
import com.safalifter.userservice.request.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserService {
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageClient fileStorageClient;
    private final ModelMapper modelMapper;

    public User saveUser(RegisterRequest request) {
        String userId = UUID.randomUUID().toString();
        
        CreateUserCommand command = CreateUserCommand.builder()
                .userId(userId)
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(Role.USER)
                .active(Active.ACTIVE)
                .build();
        
        commandGateway.sendAndWait(command);
        
        // Query the created user
        return queryGateway.query(
                new FindUserByIdQuery(userId),
                User.class
        ).join();
    }

    public List<User> getAll() {
        return queryGateway.query(
                new FindAllUsersQuery(),
                org.axonframework.messaging.responsetypes.ResponseTypes.multipleInstancesOf(User.class)
        ).join();
    }

    public User getUserById(String id) {
        return queryGateway.query(
                new FindUserByIdQuery(id),
                User.class
        ).join();
    }

    public User getUserByEmail(String email) {
        return queryGateway.query(
                new FindUserByEmailQuery(email),
                User.class
        ).join();
    }

    public User getUserByUsername(String username) {
        return queryGateway.query(
                new FindUserByUsernameQuery(username),
                User.class
        ).join();
    }

    public User updateUserById(UserUpdateRequest request, MultipartFile file) {
        User existingUser = getUserById(request.getId());
        
        UserDetails updatedDetails = updateUserDetails(existingUser.getUserDetails(), request.getUserDetails(), file);
        
        UpdateUserCommand command = UpdateUserCommand.builder()
                .userId(request.getId())
                .username(request.getUsername() != null ? request.getUsername() : existingUser.getUsername())
                .password(request.getPassword() != null ? passwordEncoder.encode(request.getPassword()) : existingUser.getPassword())
                .userDetails(updatedDetails)
                .build();
        
        commandGateway.sendAndWait(command);
        
        // Query the updated user
        return getUserById(request.getId());
    }

    public void deleteUserById(String id) {
        DeleteUserCommand command = DeleteUserCommand.builder()
                .userId(id)
                .build();
        
        commandGateway.sendAndWait(command);
    }

    protected User findUserById(String id) {
        return getUserById(id);
    }

    protected User findUserByEmail(String email) {
        return getUserByEmail(email);
    }

    protected User findUserByUsername(String username) {
        return getUserByUsername(username);
    }

    private UserDetails updateUserDetails(UserDetails toUpdate, UserDetails request, MultipartFile file) {
        toUpdate = toUpdate == null ? new UserDetails() : toUpdate;

        if (file != null) {
            String profilePicture = fileStorageClient.uploadImageToFIleSystem(file).getBody();
            if (profilePicture != null) {
                fileStorageClient.deleteImageFromFileSystem(toUpdate.getProfilePicture());
                toUpdate.setProfilePicture(profilePicture);
            }
        }

        modelMapper.map(request, toUpdate);

        return toUpdate;
    }
}