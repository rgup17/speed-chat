package com.speedchat.server.services;

import com.speedchat.server.models.entities.User;
import com.speedchat.server.repositiories.UserRepository;
import com.speedchat.server.utils.EmailManager;
import com.speedchat.server.utils.TokenManager;
import io.jsonwebtoken.Claims;
import jakarta.mail.MessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class AuthenticationService {

    private final RedissonClient redissonClient;
    private final EmailManager emailManager;
    private final TokenManager tokenManager;
    private final UserRepository userRepository;

    private final Logger LOGGER = LogManager.getLogger(AuthenticationService.class);


    public AuthenticationService(RedissonClient redissonClient, EmailManager emailManager, TokenManager tokenManager, UserRepository userRepository) {
        this.redissonClient = redissonClient;
        this.emailManager = emailManager;
        this.tokenManager = tokenManager;
        this.userRepository = userRepository;
    }


    private static String createOTP() {
        StringBuilder otp = new StringBuilder();
        Random random = new Random();

        for (int i = 0 ; i < 6; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }

    public void sendOTP(String emailAddress) throws MessagingException {
        String otp = createOTP();
        saveOTPToRedis("OTP_HASH", emailAddress, otp);
        emailManager.sendEmail(emailAddress, otp);
    }

    /**
     * Compare OTP provided to the OTP in the cache. If it matches, successfully authenticate.
     * "E" means existing user, "N" means new user.
     */
    public Pair<Object, String> verifyOTP(String emailAddress, String OTP) throws Exception {
        String otpInRedis = (String) redissonClient.getMap("OTP_HASH").get(emailAddress);
        if (otpInRedis == null) return null;
        boolean otpIsCorrect = OTP.equals(otpInRedis);
        if (!otpIsCorrect) {
            throw new Exception("OTP Verification failed. OTP is expired/invalid. Please request a new OTP to verify your email.");
        }
        deleteOTPFromRedis("OTP_HASH", emailAddress);

        String type =  "E";
        User user = userRepository.findUserByEmail(emailAddress);
        Map<String, Object> response = new HashMap<>();
        if (user == null) {
            LOGGER.info("User with email address {} does not exist", emailAddress);
            type = "N";
            userRepository.save(new User("", emailAddress));
            LOGGER.info("Created a new user for {}", emailAddress);
        }
        else {
            LOGGER.info("User with email address {} already exists", emailAddress);
            response.put("accessToken", tokenManager.generateJWTToken(user));
            response.put("user", user);
        }
        return Pair.of(response, type);
    }

    public Map<String, String> saveUsername(Long userId, String username, String email) throws Exception {
        int numberOfRowsAffected = userRepository.updateUsernameByEmail(email, username);
        if (numberOfRowsAffected == 0) throw new Exception("Failed to update username. Email not found.");
        Map<String, String> response = new HashMap<>();
        // we should create new token when user updates their username
        response.put("accessToken", tokenManager.generateJWTToken(new User(username, email)));
        return response;
    }

    public JSONObject validateUserToken(String token) {
        Claims claims = tokenManager.validateJWTToken(token);
        JSONObject userData = new JSONObject();

        // valid token since we can extract claims
        if (claims != null) {
            userData.put("userId", claims.get("userId"))
                    .put("username", claims.get("username"))
                    .put("email", claims.get("email"))
                    .put("isAuthenticated", true);
        }
        else {
            userData.put("isAuthenticated", false);
        }

        return userData;
    }

    @Async("cachedThreadPool")
    public void saveOTPToRedis(String hash, String key, Object value) {
        RMap<String, Object> rmap = redissonClient.getMap(hash);
        rmap.put(key, value);
        LOGGER.info("Successfully saved OTP To Redis");
    }

    public void deleteOTPFromRedis(String hash, String key) {
        RMap<String, Object> rmap = redissonClient.getMap(hash);
        rmap.remove(key);
        LOGGER.info("Successfully removed OTP from Redis");
    }


}
