package controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import values.ErrorMessages;
import values.Token;
import interfaces.Controller;
import interfaces.Responses;
import model.User;
import repositories.UserRepository;
import services.AccessControlService;
import spark.Spark;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserController implements Controller {
    private static final String ROUTE_GET_ALL = "/user/all";
    private static final String ROUTE_GET_USER = "/user/get/username/:username";
    private static final String ROUTE_GET_USER_BY_ID = "/user/get/:id";
    private static final String ROUTE_GET_PROFILE = "/user/profile/:username";
    private static final String ROUTE_DELETE_USER = "/user/delete/:id";                                 // header: token
    private static final String ROUTE_CREATE_USER = "/user/create";
    private static final String ROUTE_LOGIN = "/user/login";
    private static final String ROUTE_PROFILE_EDIT = "/user/profile/:username/edit";
    private static final String ROUTE_PROFILE_EDIT_PFP = "/user/profile/:username/edit/pfp";
    private static final String ROUTE_PROFILE_EDIT_BANNER = "/user/profile/:username/edit/banner";
    private static final String ROUTE_GET_FRIENDS = "/user/friends/get/:id";
    private static final String ROUTE_GET_FRIEND_STATUS = "/user/friends/status/:friend_id";            // header: token
    private static final String ROUTE_GET_PENDING_FRIENDS_REQUESTS = "/user/friends/pending/get";       // header: token
    private static final String ROUTE_GET_SENT_FRIENDS_REQUESTS = "/user/friends/sent/get";             // header: token
    private static final String ROUTE_SEND_FRIEND_REQUEST = "/user/friends/send/:friend_id";            // header: token
    private static final String ROUTE_ACCEPT_FRIEND_REQUEST = "/user/friends/accept/:friend_id";        // header: token
    private static final String ROUTE_REJECT_FRIEND_REQUEST = "/user/friends/reject/:friend_id";        // header: token
    private static final String ROUTE_REMOVE_FRIEND = "/user/friends/remove/:friend_id";                // header: token

    private final EntityManagerFactory factory;
    private static UserController userController;

    public static UserController getInstance(EntityManagerFactory factory) {
        if (userController == null) {
            userController = new UserController(factory);
        }
        return userController;
    }

    private UserController(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public void run() {
        setRouteGetAll();
        setRouteGetUserById();
        setRouteDeleteUser();
        setRouteEditProfile();
        setRouteEditPfp();
        setRouteEditBanner();
        setRouteGetFriends();
        setRouteGetFriendStatus();
        setRouteGetPendingFriendsRequests();
        setRouteGetSentFriendsRequests();
        setRouteSendFriendRequest();
        setRouteAcceptFriendRequest();
        setRouteRejectFriendRequest();
        setRouteRemoveFriend();
    }

    private void setRouteGetAll() {
        Spark.get(ROUTE_GET_ALL, "application/json", (req, resp) -> {   // accepts json in the body, doesn't use it tho
                                                        // (request, response)

            resp.type("application/json");           // assign the type of data it returns
            resp.status(200);                         // status 200 = request successful

            EntityManager em = factory.createEntityManager();       // create entityManager
            UserRepository userRepository = new UserRepository(em); // pass it to the repository to access BD

            JsonArray jsonArray = new JsonArray();              // prepare response
            for (User user : userRepository.listAll()) {        // add all users
                jsonArray.add(user.asJson());
            }

            em.close();                                         // close connection to BD (maybe)
            return jsonArray.toString();
        });
    }

    private void setRouteGetUserById() {
        Spark.get(ROUTE_GET_USER_BY_ID, "application/json", (req, resp) -> {
            EntityManager em = factory.createEntityManager();
            
            long userId;
            try {
                userId = Long.parseLong(req.params(":id"));
            } catch (NumberFormatException e) {
                resp.status(403);
                return ErrorMessages.informationNotNumber("User ID");
            }
    
            UserRepository userRepository = new UserRepository(em);
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                resp.status(404);
                return ErrorMessages.informationNotFound("User");
            }
        
            resp.type("application/json");
            resp.status(200);
        
            em.close();
            return user.get().asJsonProfile();
        });
    }
    
    private void setRouteDeleteUser() {
        Spark.post(ROUTE_DELETE_USER, "application/json", (req, resp) -> {
            EntityManager em = factory.createEntityManager();
    
            String token = req.headers(Token.PROPERTY_NAME);
            if (token == null || !AccessControlService.isTokenValid(token)) {
                resp.status(401);
                return ErrorMessages.userMustBeLoggedIn();
            }
            String username = AccessControlService.getUsernameFromToken(token);
            Optional<User> user = new UserRepository(em).findByUsername(username);
    
            if (user.isEmpty()) {
                resp.status(404);
                return ErrorMessages.usernameNotFound(username);
            }
            
            long userId;
            try {
                userId = Long.parseLong(req.params(":id"));
            } catch (NumberFormatException e) {
                resp.status(403);
                return ErrorMessages.informationNotNumber("User ID");
            }
            
            UserRepository userRepository = new UserRepository(em);
            Optional<User> userToBeDeleted = userRepository.findById(userId);
            if (userToBeDeleted.isEmpty()) {
                resp.status(404);
                return ErrorMessages.informationNotFound("User To Be Deleted");
            }
            
            resp.type("application/json");
            resp.status(204);
            
            userRepository.deleteUserByID(userId);
            
            em.close();
            return "";
        });
    }
    
    private void setRouteEditProfile() {
        Spark.post(ROUTE_PROFILE_EDIT, "application/json", (req, resp) -> {
            // body: username, biography, pfp, banner | header: token
            EntityManager em = factory.createEntityManager();

            String token = req.headers(Token.PROPERTY_NAME);
            if (token == null || !AccessControlService.isTokenValid(token)) {
                resp.status(401);
                return ErrorMessages.userMustBeLoggedIn();
            }

            String username = AccessControlService.getUsernameFromToken(token);
            UserRepository userRepository = new UserRepository(em);
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                resp.status(404);
                return ErrorMessages.usernameNotFound(username);
            }

            JsonObject body = JsonParser
                    .parseString(req.body())
                    .getAsJsonObject();

            JsonElement newUsernameElem = body.get("username");
            if (newUsernameElem != null) {
                String newUsername = newUsernameElem.getAsString();

                if (!username.equals(newUsername)) {
                    Responses response = AccessControlService.isUsernameValid(newUsername);
                    if (response.hasError()) {
                        resp.status(response.getStatusCode());
                        return response.getMessage();
                    }
                    if (userRepository.findByUsername(newUsername).isPresent()) {
                        resp.status(400);
                        return ErrorMessages.usernameAlreadyInUse(username);
                    }
                    user.get().setUsername(newUsername);
                    token = AccessControlService.generateToken(newUsername).getToken();
                }
            }

            String oldBio = user.get().getBiography();
            JsonElement newBioElem = body.get("biography");
            if (newBioElem != null) {
                String newBio = newBioElem.getAsString();
                if (!newBio.equals(oldBio)) {
                    user.get().setBiography(newBio);
                }
            }

            String oldPfp = user.get().getPfp();
            JsonElement newPfpElem = body.get("pfp");
            if (newPfpElem != null) {
                String newPfp = newPfpElem.getAsString();
                if (!newPfp.equals(oldPfp)) {
                    user.get().setPfp(newPfp);
                }
            }

            String oldBanner = user.get().getBanner();
            JsonElement newBannerElem = body.get("banner");
            if (newBannerElem != null) {
                String newBanner = newBannerElem.getAsString();
                if (!newBanner.equals(oldBanner)) {
                    user.get().setBanner(newBanner);
                }
            }

            userRepository.persist(user.get());

            resp.status(200);
            resp.type("application/json");

            JsonObject jsonObject = user.get().asJsonProfile();
            jsonObject.addProperty(Token.PROPERTY_NAME, token);

            em.close();
            return jsonObject;
        });
    }
    
    private void setRouteEditPfp() {
        Spark.post(ROUTE_PROFILE_EDIT_PFP, "application/json", (req, resp) -> { // :username | body: pfp
            EntityManager em = factory.createEntityManager();
        
            String username = req.params(":username");
            UserRepository userRepository = new UserRepository(em);
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                resp.status(404);
                return ErrorMessages.usernameNotFound(username);
            }
        
            JsonObject body = JsonParser
                .parseString(req.body())
                .getAsJsonObject();
            
            String pfp = body.get("pfp").getAsString();
            user.get().setPfp(pfp);
            userRepository.persist(user.get());

            resp.status(200);
            resp.type("application/json");
        
            em.close();
            return user.get().asJsonProfile();
        });
    }
    
    private void setRouteEditBanner() {
        Spark.post(ROUTE_PROFILE_EDIT_BANNER, "application/json", (req, resp) -> { // :username | body: banner
            EntityManager em = factory.createEntityManager();
            
            String username = req.params(":username");
            UserRepository userRepository = new UserRepository(em);
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                resp.status(404);
                return ErrorMessages.usernameNotFound(username);
            }
            
            JsonObject body = JsonParser
                .parseString(req.body())
                .getAsJsonObject();
            
            String banner = body.get("banner").getAsString();
            user.get().setBanner(banner);
            userRepository.persist(user.get());

            resp.status(200);
            resp.type("application/json");
            
            em.close();
            return user.get().asJsonProfile();
        });
    }

    private void setRouteGetFriends() {
        Spark.get(ROUTE_GET_FRIENDS, "application/json", (req, resp) -> {   // :id
            EntityManager em = factory.createEntityManager();

            long userId;
            try {
                userId = Long.parseLong(req.params(":id"));
            } catch (NumberFormatException e) {
                resp.status(403);
                return ErrorMessages.informationNotNumber("User ID");
            }

            UserRepository userRepository = new UserRepository(em);
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                resp.status(404);
                return ErrorMessages.informationNotFound("User");
            }

            resp.type("application/json");
            resp.status(200);

            List<User> friends = new ArrayList<>(user.get().getFriends());
            JsonArray jsonFriends = new JsonArray();

            friends.forEach(f -> jsonFriends.add(f.asJsonProfile()));
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("friends", jsonFriends);

            em.close();
            return jsonObject;
        });
    }
    
    private void setRouteGetFriendStatus() {
        Spark.get(ROUTE_GET_FRIEND_STATUS, "application/json", (req, resp) -> {   // :friend_id | header: token
            EntityManager em = factory.createEntityManager();
    
            String token = req.headers(Token.PROPERTY_NAME);
            if (token == null || !AccessControlService.isTokenValid(token)) {
                resp.status(401);
                return ErrorMessages.userMustBeLoggedIn();
            }
            String username = AccessControlService.getUsernameFromToken(token);
    
            UserRepository userRepository = new UserRepository(em);
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                resp.status(404);
                return ErrorMessages.informationNotFound("User");
            }
            
            long userId;
            try {
                userId = Long.parseLong(req.params(":friend_id"));
            } catch (NumberFormatException e) {
                resp.status(403);
                return ErrorMessages.informationNotNumber("User ID");
            }
            
            Optional<User> friend = userRepository.findById(userId);
            if (friend.isEmpty()) {
                resp.status(404);
                return ErrorMessages.informationNotFound("Friend");
            }
            
            resp.type("application/json");
            resp.status(200);
            
            boolean isFriend = user.get().getFriends().contains(friend.get());
            boolean isPending = user.get().getFriendRequestsPending().contains(friend.get());
            boolean isSent = user.get().getFriendRequestsSent().contains(friend.get());
            
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("is_friend", isFriend);
            jsonObject.addProperty("is_pending", isPending);
            jsonObject.addProperty("is_sent", isSent);
            
            em.close();
            return jsonObject;
        });
    }

    private void setRouteGetPendingFriendsRequests() {
        Spark.get(ROUTE_GET_PENDING_FRIENDS_REQUESTS, "application/json", (req, resp) -> {   // header: token
            EntityManager em = factory.createEntityManager();

            String token = req.headers(Token.PROPERTY_NAME);
            if (token == null || !AccessControlService.isTokenValid(token)) {
                resp.status(401);
                return ErrorMessages.userMustBeLoggedIn();
            }
            String username = AccessControlService.getUsernameFromToken(token);

            UserRepository userRepository = new UserRepository(em);
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                resp.status(404);
                return ErrorMessages.informationNotFound("User");
            }

            resp.type("application/json");
            resp.status(200);

            List<User> pending = new ArrayList<>(user.get().getFriendRequestsPending());
            JsonArray jsonPending = new JsonArray();
            pending.forEach(u -> jsonPending.add(u.asJsonProfile()));

            JsonObject jsonObject = new JsonObject();
            jsonObject.add("pending_requests", jsonPending);

            em.close();
            return jsonObject;
        });
    }

    private void setRouteGetSentFriendsRequests() {
        Spark.get(ROUTE_GET_SENT_FRIENDS_REQUESTS, "application/json", (req, resp) -> {   // header: token
            EntityManager em = factory.createEntityManager();

            String token = req.headers(Token.PROPERTY_NAME);
            if (token == null || !AccessControlService.isTokenValid(token)) {
                resp.status(401);
                return ErrorMessages.userMustBeLoggedIn();
            }
            String username = AccessControlService.getUsernameFromToken(token);

            UserRepository userRepository = new UserRepository(em);
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                resp.status(404);
                return ErrorMessages.informationNotFound("User");
            }

            resp.type("application/json");
            resp.status(200);

            List<User> pending = new ArrayList<>(user.get().getFriendRequestsSent());
            JsonArray jsonSent = new JsonArray();
            pending.forEach(u -> jsonSent.add(u.asJsonProfile()));

            JsonObject jsonObject = new JsonObject();
            jsonObject.add("sent_requests", jsonSent);

            em.close();
            return jsonObject;
        });
    }

    private void setRouteSendFriendRequest() {
        Spark.put(ROUTE_SEND_FRIEND_REQUEST, "application/json", (req, resp) -> {   // :friend_id   | header: token
            EntityManager em = factory.createEntityManager();

            String token = req.headers(Token.PROPERTY_NAME);
            if (token == null || !AccessControlService.isTokenValid(token)) {
                resp.status(401);
                return ErrorMessages.userMustBeLoggedIn();
            }
            String username = AccessControlService.getUsernameFromToken(token);

            long friendId;
            try {
                friendId = Long.parseLong(req.params(":friend_id"));
            } catch (NumberFormatException e) {
                resp.status(403);
                return ErrorMessages.informationNotNumber("Friend ID");
            }

            UserRepository userRepository = new UserRepository(em);
            Optional<User> friend = userRepository.findById(friendId);
            if (friend.isEmpty()) {
                resp.status(404);
                return ErrorMessages.informationNotFound("Friend");
            }

            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                resp.status(404);
                return ErrorMessages.informationNotProvided("User");
            }

            user.get().sendFriendRequest(friend.get());
            userRepository.persist(user.get());
            userRepository.persist(friend.get());

            resp.type("application/json");
            resp.status(204);

            em.close();
            return "";
        });
    }

    private void setRouteAcceptFriendRequest() {
        Spark.put(ROUTE_ACCEPT_FRIEND_REQUEST, "application/json", (req, resp) -> {   // :friend_id   | header: token
            EntityManager em = factory.createEntityManager();

            String token = req.headers(Token.PROPERTY_NAME);
            if (token == null || !AccessControlService.isTokenValid(token)) {
                resp.status(401);
                return ErrorMessages.userMustBeLoggedIn();
            }
            String username = AccessControlService.getUsernameFromToken(token);

            long friendId;
            try {
                friendId = Long.parseLong(req.params(":friend_id"));
            } catch (NumberFormatException e) {
                resp.status(403);
                return ErrorMessages.informationNotNumber("Friend ID");
            }

            UserRepository userRepository = new UserRepository(em);
            Optional<User> friend = userRepository.findById(friendId);
            if (friend.isEmpty()) {
                resp.status(404);
                return ErrorMessages.informationNotFound("Friend");
            }

            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                resp.status(404);
                return ErrorMessages.informationNotProvided("User");
            }

            if (!user.get().getFriendRequestsPending().contains(friend.get())) {
                resp.status(404);
                return ErrorMessages.informationNotFound("Friend Request");
            }

            user.get().acceptFriendRequest(friend.get());
            userRepository.persist(user.get());
            userRepository.persist(friend.get());

            resp.type("application/json");
            resp.status(204);

            em.close();
            return "";
        });
    }

    private void setRouteRejectFriendRequest() {
        Spark.put(ROUTE_REJECT_FRIEND_REQUEST, "application/json", (req, resp) -> {   // :friend_id   | header: token
            EntityManager em = factory.createEntityManager();

            String token = req.headers(Token.PROPERTY_NAME);
            if (token == null || !AccessControlService.isTokenValid(token)) {
                resp.status(401);
                return ErrorMessages.userMustBeLoggedIn();
            }
            String username = AccessControlService.getUsernameFromToken(token);

            long friendId;
            try {
                friendId = Long.parseLong(req.params(":friend_id"));
            } catch (NumberFormatException e) {
                resp.status(403);
                return ErrorMessages.informationNotNumber("Friend ID");
            }

            UserRepository userRepository = new UserRepository(em);
            Optional<User> friend = userRepository.findById(friendId);
            if (friend.isEmpty()) {
                resp.status(404);
                return ErrorMessages.informationNotFound("Friend");
            }

            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                resp.status(404);
                return ErrorMessages.informationNotProvided("User");
            }

            if (!user.get().getFriendRequestsPending().contains(friend.get())) {
                resp.status(404);
                return ErrorMessages.informationNotFound("Friend Request");
            }

            user.get().rejectFriendRequest(friend.get());
            userRepository.persist(user.get());
            userRepository.persist(friend.get());

            resp.type("application/json");
            resp.status(204);

            em.close();
            return "";
        });
    }

    private void setRouteRemoveFriend() {
        Spark.put(ROUTE_REMOVE_FRIEND, "application/json", (req, resp) -> {   // :friend_id   | header: token
            EntityManager em = factory.createEntityManager();

            String token = req.headers(Token.PROPERTY_NAME);
            if (token == null || !AccessControlService.isTokenValid(token)) {
                resp.status(401);
                return ErrorMessages.userMustBeLoggedIn();
            }
            String username = AccessControlService.getUsernameFromToken(token);

            long friendId;
            try {
                friendId = Long.parseLong(req.params(":friend_id"));
            } catch (NumberFormatException e) {
                resp.status(403);
                return ErrorMessages.informationNotNumber("Friend ID");
            }

            UserRepository userRepository = new UserRepository(em);
            Optional<User> friend = userRepository.findById(friendId);
            if (friend.isEmpty()) {
                resp.status(404);
                return ErrorMessages.informationNotFound("Friend");
            }

            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                resp.status(404);
                return ErrorMessages.informationNotProvided("User");
            }

            if (!user.get().getFriends().contains(friend.get())) {
                resp.status(404);
                return ErrorMessages.informationNotFound("Friend");
            }

            user.get().removeFriend(friend.get());
            userRepository.persist(user.get());
            userRepository.persist(friend.get());

            resp.type("application/json");
            resp.status(204);

            em.close();
            return "";
        });
    }
}
