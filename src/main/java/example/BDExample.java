package example;

import values.Rol;
import values.TagType;
import model.*;
import repositories.*;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class BDExample {
    private static Set<User> allUsers = new HashSet<>();
    private final EntityManager entityManager;

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
            name.append("Game #" + (games.size() + 100));
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

            game.setDescription(desc.toString(), LocalDate.now());
            games.add(game);
        }
        //

        UserRepository userRepository = new UserRepository(entityManager);
        allUsers.forEach(userRepository::persist);

        TagRepository tagRepository = new TagRepository(entityManager);
        tags.forEach(tagRepository::persist);

        GameRepository gameRepository = new GameRepository(entityManager);
        games.forEach(gameRepository::persist);

        ReviewRepository reviewRepository = new ReviewRepository(entityManager);
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);

            if (i > reviews.size() / 2) {
                break;
            }
            game.addReview(reviews.get(i));
            game.addReview(reviews.get(reviews.size() - 1 - i));

            reviews.forEach(r -> {
                if (r.getGame() != null) reviewRepository.addReview(r, r.getAuthor(), r.getGame());
            });
        }

        // shelves
        List<Shelf> shelves = getExampleShelf(new ArrayList<>(allUsers));
        ShelfRepository shelfRepository = new ShelfRepository(entityManager);
        for (Shelf shelf : shelves) {
            shelfRepository.persist(shelf);
            for (int i = (int) (Math.random() * 5); i < games.size(); i += 3) {
                shelfRepository.addGame(shelf, shelf.getOwner(), games.get(i));
            }
        }

        indies();
        paymentExample();
    }

    static {
        allUsers.addAll(getExampleUsers(Rol.USER, 700, 900));
    }

    public void indies() {
        TagRepository tagRepository = new TagRepository(entityManager);
        Tag indie = new Tag("Indie");
        indie.setId(9999L);
        tagRepository.persist(indie);

        Developer dev = new Developer(new User(
                "AwesomeDev",
                "",
                "password",
                Rol.DEVELOPER
        ));
        UserRepository userRepository = new UserRepository(entityManager);
        userRepository.persist(dev.getUser());
        DeveloperRepository developerRepository = new DeveloperRepository(entityManager);
        developerRepository.persist(dev);

        Optional<Tag> multi = tagRepository.findByName("Multiplayer");

        GameRepository gameRepository = new GameRepository(entityManager);


        Game stardew = new Game(
                "Stardew Valley",
                dev.getUser(),
                "You've inherited your grandfather's old farm plot in Stardew Valley. Armed with hand-me-down tools and a few coins, you set out to begin your new life. Can you learn to live off the land and turn these overgrown fields into a thriving home?",
                LocalDate.of(2016, Month.FEBRUARY, 26),
                ImageExample.getStardewCover(),
                ImageExample.getStardewBkgd()
        );
        gameRepository.persist(stardew);

        Optional<Tag> cozy = tagRepository.findByName("Cozy game");
        cozy.ifPresent(tag -> gameRepository.addTag(dev.getUser(), stardew, tag));
        multi.ifPresent(tag -> gameRepository.addTag(dev.getUser(), stardew, tag));
        gameRepository.addTag(dev.getUser(), stardew, indie);

        Game cuphead = new Game(
                "Cuphead",
                dev.getUser(),
                "Cuphead is a classic run and gun action game heavily focused on boss battles. Inspired by cartoons of the 1930s, the visuals and audio are painstakingly created with the same techniques of the era, i.e. traditional hand drawn cel animation, watercolor backgrounds, and original jazz recordings.",
                LocalDate.of(2017, Month.SEPTEMBER, 7),
                ImageExample.getCupheadCover(),
                ImageExample.getCupheadBkgd()
        );
        gameRepository.persist(cuphead);
        multi.ifPresent(tag -> gameRepository.addTag(dev.getUser(), cuphead, tag));
        gameRepository.addTag(dev.getUser(), cuphead, indie);


        Game pz = new Game(
                "Project Zomboid",
                dev.getUser(),
                "Project Zomboid is the ultimate in zombie survival. Alone or in MP: you loot, build, craft, fight, farm and fish in a struggle to survive. A hardcore RPG skillset, a vast map, massively customisable sandbox and a cute tutorial raccoon await the unwary. So how will you die? All it takes is a bite..",
                LocalDate.of(2013, Month.NOVEMBER, 8),
                ImageExample.getPzCover(),
                ImageExample.getPzBkgd()
        );
        gameRepository.persist(pz);
        Optional<Tag> survival = tagRepository.findByName("Survival");
        survival.ifPresent(tag -> gameRepository.addTag(dev.getUser(), pz, tag));
        multi.ifPresent(tag -> gameRepository.addTag(dev.getUser(), pz, tag));
        gameRepository.addTag(dev.getUser(), pz, indie);
    }

    public static List<Tag> getExampleTags() {
        List<String> platforms = List.of("Steam", "Riot", "PlayStation", "Xbox", "EpicGames", "Mobile");
        List<String> genres = List.of(
                "Action",
                "Fighting",
                "Shooter",
                "Survival",
                "Battle Royale",
                "Adventure",
                "Horror",
                "Cozy game",
                "Multiplayer");

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
            if (Math.random() < 0.4) {
                Review r = new Review("I, " + user.getUsername() + ", think that this game is very game :)");
                r.setAuthor(user);
                reviews.add(r);
            }
            if (Math.random() < 0.4) {
                Review r = new Review("I, " + user.getUsername() + ", think that this game is kinda trash >:(");
                r.setAuthor(user);
                reviews.add(r);
            }
        }
        Collections.shuffle(reviews);
        return reviews;
    }

    public static List<Shelf> getExampleShelf(List<User> owners) {
        List<Shelf> shelves = new ArrayList<>();

        for (int i = 0; i < owners.size(); i += 2) {
            User owner = owners.get(i);
            String name = owner.getUsername() + "'s shelf";
            shelves.add(new Shelf(owner, name));
            if (i % 4 == 0) {
                name = owner.getUsername() + "'s second shelf";
                shelves.add(new Shelf(owner, name));
            }
            if (i % 8 == 0) {
                name = owner.getUsername() + "'s third shelf";
                shelves.add(new Shelf(owner, name));
            }
        }
        return shelves;
    }
    
    private void paymentExample() {
        Developer dev1 = new Developer(new User(
            "IAcceptDonations1",
            "gamelib.bot+donations1@gmail.com",
            "1234",
            Rol.DEVELOPER
        ));
    
        Developer dev2 = new Developer(new User(
            "IAcceptDonations2",
            "gamelib.bot+donations2@gmail.com",
            "1234",
            Rol.DEVELOPER
        ));
    
        /*------------------*/
    
        UserRepository userRepository = new UserRepository(entityManager);
        userRepository.persist(dev1.getUser());
        userRepository.persist(dev2.getUser());
    
        /*------------------*/
        
        DeveloperRepository developerRepository = new DeveloperRepository(entityManager);
        developerRepository.setupDonations(
            dev1.getUser(),
            "APP_USR-7935a1bd-06e9-4148-ac35-6d3efc60dbdf",
            "APP_USR-3308950100823866-070817-edee83028d45f993e55a22ec7b955a62-1893394530"
        );
        developerRepository.setupDonations(
            dev2.getUser(),
            "APP_USR-d5fc5ac3-de97-452d-9138-d247e7aeac00",
            "APP_USR-3163159331731443-071018-7032e41a4154a40c58275390f87c992b-1894251913"
        );
    }
}
