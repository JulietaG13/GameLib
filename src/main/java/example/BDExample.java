package example;

import entities.Rol;
import entities.TagType;
import model.Game;
import model.Review;
import model.Tag;
import model.User;
import services.GameService;
import services.ReviewService;
import services.TagService;
import services.UserService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BDExample {
    private static Set<User> allUsers = new HashSet<>();
    private EntityManager entityManager;

    public BDExample(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void store() {
        // games
        List<Game> games = new ArrayList<>();
        List<Tag> tags = getExampleTags();
        List<Review> reviews = getExampleReviews();

        List<User> devs = getExampleUsers(Rol.DEVELOPER, 10, 90);

        for (int i = 0; i < tags.size(); i++) {
            Set<Tag> tagsForGame = new HashSet<>();
            tagsForGame.add(tags.get(i));
            for (int j = 0; j < tags.size(); j += tags.size() / 4) {
                tagsForGame.add(tags.get((i + j) % tags.size()));
            }

            StringBuilder name = new StringBuilder();
            for (Tag tag : tagsForGame) {
                name.append(tag.getName());
            }
            Game game = Game.create(name.toString()).build();
            tagsForGame.forEach(game::addTag);

            game.setOwner(devs.get((int) (Math.random() * devs.size())));

            StringBuilder desc = new StringBuilder();
            desc.append("Game developed by: ").append(game.getOwner().getUsername()).append(". ");
            desc.append("Tags: ");
            for (Tag tag : tagsForGame) {
                desc.append(tag.getName()).append(", ");
            }
            desc.append(". ");
            desc.append("And it has ").append(reviews.size()).append(" reviews.");

            game.setDescription(desc.toString(), LocalDateTime.now());
            games.add(game);
        }
        //

        UserService userService = new UserService(entityManager);
        allUsers.forEach(userService::persist);

        TagService tagService = new TagService(entityManager);
        tags.forEach(tagService::persist);

        GameService gameService = new GameService(entityManager);
        games.forEach(gameService::persist);

        ReviewService reviewService = new ReviewService(entityManager);
        for (Game game : games) {
            List<Review> reviewsForGame = reviews.stream()
                    .filter(r -> Math.random() < 0.2)
                    .collect(Collectors.toList());

            reviewsForGame.forEach(game::addReview);
            reviews.forEach(r -> reviewService.addReview(r, r.getAuthor(), game));
        }
    }

    static {
        allUsers.addAll(getExampleUsers(Rol.USER, 700, 900));
    }

    public static List<Tag> getExampleTags() {
        List<String> platforms = List.of("Steam", "Riot", "PlayStation", "Xbox", "EpicGames", "Mobile", "Friv");
        List<String> genres = List.of(
                "Action",
                "Fighting",
                "Shooter",
                "Survival",
                "Battle Royale",
                "Adventure",
                "Horror",
                "MMO",
                "Role-playing",
                "Strategy",
                "Cozy game");

        List<Tag> tags = new ArrayList<>(platforms.size() + genres.size());

        for(String platform : platforms) {
            tags.add(new Tag(platform, TagType.PLATFORM));
        }

        for(String genre : genres) {
            tags.add(new Tag(genre, TagType.GENRE));
        }
        return tags;
    }

    public static List<User> getExampleUsers(Rol rol, int from, int to) {
        List<User> users = new ArrayList<>();

        for (int i = from; i < to; i+= 8) {
            User u = User.create("username" + i)
                    .email(i + "@example.com")
                    .password("qwerty" + i)
                    .rol(rol)
                    .build();
            users.add(u);
        }
        allUsers.addAll(users);
        return users;
    }

    public static List<Review> getExampleReviews() {
        List<Review> reviews = new ArrayList<>();

        for (User user : allUsers) {
            if (Math.random() < 0.2) continue;
            Review r1 = new Review("I, " + user.getUsername() + ", think that this game is very game :)");
            Review r2 = new Review("I, " + user.getUsername() + ", think that this game is kinda trash >:(");
            r1.setAuthor(user);
            r2.setAuthor(user);
            reviews.add(r1);
            reviews.add(r2);
        }
        return reviews;
    }
}
