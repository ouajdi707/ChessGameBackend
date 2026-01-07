package com.example.test.controller;

import com.example.test.dto.OnlineUserResponse;
import com.example.test.service.OnlineUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private OnlineUserService onlineUserService;

    @GetMapping("/online")
    public ResponseEntity<List<OnlineUserResponse>> getOnlineUsers(@RequestParam(required = false) String currentUser) {
        List<OnlineUserResponse> onlineUsers = onlineUserService.getOnlineUsers().stream()
                .filter(username -> currentUser == null || !username.equals(currentUser))
                .map(username -> new OnlineUserResponse(username, true))
                .collect(Collectors.toList());
        return ResponseEntity.ok(onlineUsers);
    }

    @GetMapping("/online/count")
    public ResponseEntity<Integer> getOnlineCount() {
        return ResponseEntity.ok(onlineUserService.getOnlineCount());
    }

    @PostMapping("/online/{username}")
    public ResponseEntity<?> setUserOnline(@PathVariable String username) {
        onlineUserService.addUser(username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/online/{username}")
    public ResponseEntity<?> setUserOffline(@PathVariable String username) {
        onlineUserService.removeUser(username);
        return ResponseEntity.ok().build();
    }
}

