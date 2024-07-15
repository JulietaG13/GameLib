package demo;

import example.ImageExample;
import model.*;
import repositories.*;
import values.Rol;
import values.TagType;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDemo {
  
  private final EntityManagerFactory factory;
  
  public DatabaseDemo(EntityManagerFactory factory) {
    this.factory = factory;
  }
  
  public void run() {
    func();
  }
  
  private void func() {
    EntityManager em = factory.createEntityManager();
  
    Developer developer1 = new Developer(new User(
        "IOwnGames",
        "gamelib.test+IOwnGames@gmail.com",
        "1234",
        Rol.DEVELOPER
    ));
  
    Developer developer2 = new Developer(new User(
        "IAlsoOwnGames",
        "gamelib.test+IAlsoOwnGames@gmail.com",
        "1234",
        Rol.DEVELOPER
    ));
  
    developer1.getUser().setPfp("https://i.natgeofe.com/n/548467d8-c5f1-4551-9f58-6817a8d2c45e/NationalGeographic_2572187_square.jpg");
    developer1.getUser().setBanner("https://t4.ftcdn.net/jpg/05/35/71/33/360_F_535713376_F10In0XLEXIqcVRFAQXIaLJ8RDvL5ynr.jpg");
  
    developer2.getUser().setPfp("https://tr.rbxcdn.com/c75f3664783f2f43e441b5f84e083a52/420/420/Hat/Png");
    developer2.getUser().setBanner("https://img.freepik.com/free-photo/front-view-beautiful-dog-with-copy-space_23-2148786562.jpg");
  
    UserRepository userRepository = new UserRepository(em);
    userRepository.persist(developer1.getUser());
    userRepository.persist(developer2.getUser());
  
    DeveloperRepository developerRepository = new DeveloperRepository(em);
    developer1 = developerRepository.findByUsername(developer1.getUser().getUsername()).get();
    developer2 = developerRepository.findByUsername(developer2.getUser().getUsername()).get();
  
    developerRepository.setupDonations(
        developer1.getUser(),
        "APP_USR-7935a1bd-06e9-4148-ac35-6d3efc60dbdf",
        "APP_USR-3308950100823866-070817-edee83028d45f993e55a22ec7b955a62-1893394530"
    );
  
    developerRepository.setupDonations(
        developer2.getUser(),
        "APP_USR-d5fc5ac3-de97-452d-9138-d247e7aeac00",
        "APP_USR-3163159331731443-071018-7032e41a4154a40c58275390f87c992b-1894251913"
    );
  
    /* GAMES */
  
    List<Game> games = new ArrayList<>();
    List<Game> gamesDev1 = new ArrayList<>();
    List<Game> gamesDev2 = new ArrayList<>();
  
  
    /* developer 1 */
  
    Game gameCult = new Game(
        "Cult of the Lamb",
        developer1.getUser(),
        "Start your own cult in a land of false prophets, " +
            "venturing out into diverse and mysterious regions to build a loyal community of woodland Followers and spread your Word to become the one true cult.",
        LocalDate.of(2022, Month.AUGUST, 11),
        "https://d28hgpri8am2if.cloudfront.net/book_images/onix/cvr9781637155226/cult-of-the-lamb-vol-1-9781637155226_hr.jpg",
        "https://www.nintendo.com/eu/media/images/10_share_images/games_15/nintendo_switch_download_software_1/2x1_NSwitchDS_CultOfTheLamb_image1280w.jpg"
    );
    games.add(gameCult);
    gamesDev1.add(gameCult);
  
    Game gameStardew = new Game(
        "Stardew Valley",
        developer1.getUser(),
        "You've inherited your grandfather's old farm plot in Stardew Valley. " +
            "Armed with hand-me-down tools and a few coins, you set out to begin your new life. " +
            "Can you learn to live off the land and turn these overgrown fields into a thriving home?",
        LocalDate.of(2016, Month.FEBRUARY, 26),
        "https://image.api.playstation.com/cdn/UP2456/CUSA06840_00/0WuZecPtRr7aEsQPv2nJqiPa2ZvDOpYm.png",
        "https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/413150/header.jpg?t=1711128146"
    );
    games.add(gameStardew);
    gamesDev1.add(gameStardew);
  
    Game gamePz = new Game(
        "Project Zomboid",
        developer1.getUser(),
        "Project Zomboid is the ultimate in zombie survival. " +
            "Alone or in MP: you loot, build, craft, fight, farm and fish in a struggle to survive. " +
            "A hardcore RPG skillset, a vast map, massively customisable sandbox and a cute tutorial raccoon await the unwary. " +
            "So how will you die? All it takes is a bite..",
        LocalDate.of(2013, Month.NOVEMBER, 8),
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQT2GM9h1UdmCbZ7x9p5YI2YnAXIA56cds1EQ&s",
        "https://static1.cbrimages.com/wordpress/wp-content/uploads/2022/01/project-zomboid-feature-header.jpg"
    );
    games.add(gamePz);
    gamesDev1.add(gamePz);
  
    Game gameCuphead = new Game(
        "Cuphead",
        developer1.getUser(),
        "Cuphead is a classic run and gun action game heavily focused on boss battles. " +
            "Inspired by cartoons of the 1930s, the visuals and audio are painstakingly created with the same techniques of the era, " +
            "i.e. traditional hand drawn cel animation, watercolor backgrounds, and original jazz recordings.",
        LocalDate.of(2017, Month.SEPTEMBER, 7),
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRvzxzzzv1n3s5IX02I2HxT4heI_AU2ie81aA&s",
        "https://assets.nintendo.com/image/upload/ar_16:9,c_lpad,w_1240/b_white/f_auto/q_auto/ncom/software/switch/70010000016330/d94d2186ef03c930392253c83c84af0c73b7e57cd902a526b09b4155a25930fe"
    );
    games.add(gameCuphead);
    gamesDev1.add(gameCuphead);
  
    Game gameLethal = new Game(
        "Lethal Company",
        developer1.getUser(),
        "A co-op horror about scavenging at abandoned moons to sell scrap to the Company.",
        LocalDate.of(2023, Month.OCTOBER, 23),
        "https://i.3djuegos.com/juegos/19403/lethal_company/fotos/ficha/lethal_company-5851601.jpg",
        "https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1966720/capsule_616x353.jpg?t=1700231592"
    );
    games.add(gameLethal);
    gamesDev1.add(gameLethal);
  
    /* developer 2 */
  
    Game gamePotion = new Game(
        "Potion Craft",
        developer2.getUser(),
        "Potion Craft is an alchemist simulator where you physically interact with your tools and ingredients to brew potions. " +
            "You're in full control of the whole shop: invent new recipes, attract customers and experiment to your heart's content. " +
            "Just remember: the whole town is counting on you.",
        LocalDate.of(2022, Month.DECEMBER, 13),
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQSHo8hM0tMwB98OJmoNETw3gxSurGbk5iqxQ&s",
        "https://assets.nintendo.com/image/upload/c_fill,w_1200/q_auto:best/f_auto/dpr_2.0/ncom/software/switch/70010000054960/72d903cda7d38d9eceb980cd9ea3fa5bc6f9733fc872501795ddd8c142b34d72"
    );
    games.add(gamePotion);
    gamesDev2.add(gamePotion);
  
    Game gameStarve = new Game(
        "Don't Starve Together",
        developer2.getUser(),
        "Fight, Farm, Build and Explore Together in the standalone multiplayer expansion to the uncompromising wilderness survival game, Don't Starve.",
        LocalDate.of(2016, Month.APRIL, 21),
        "https://image.api.playstation.com/cdn/UP2107/CUSA04236_00/39E95ckFs1PkxIFi9Ge0pRGNGmjLNB07.png",
        "https://store-images.s-microsoft.com/image/apps.47843.68986806511725911.f424da40-674e-41a9-878c-7a524fa56895.f1496447-da74-4070-887c-e8cd3e366e38?q=90&w=480&h=270"
    );
    games.add(gameStarve);
    gamesDev2.add(gameStarve);
  
    Game gameCats = new Game(
        "100 Capitalist Cats",
        developer2.getUser(),
        "100 Capitalist Cats - Join the cutest adventure in the Wall Street! " +
            "Explore the charming hand-drawn artwork of New York, heart of Capitalism, as you embark on a quest to find 100 adorable cats hidden throughout the game. " +
            "Can you find them all?",
        LocalDate.of(2024, Month.JANUARY, 21),
        "https://community.akamai.steamstatic.com/economy/image/m-sUZpfXznrMC7g4ni76g4SZki9xrJJ69mzS7cKsViBV0duYbjLRSepbrz8pH3v_gjaM4na8QmOU_JxV8d7UgcMvtQW0BKLpF2H1K-GBYXDFnMmX112vkyFEXM1ueHady76IGyc5QQ/256fx256f",
        "https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/2743080/ss_7a392b1e84261af2cac5654b878da9c4dfc88cdc.1920x1080.jpg?t=1715193555"
    );
    games.add(gameCats);
    gamesDev2.add(gameCats);
  
    Game gameTakesTwo = new Game(
        "It Takes Two",
        developer2.getUser(),
        "Embark on the craziest journey of your life in It Takes Two. " +
            "Invite a friend to join for free with Friend’s Pass and work together across a huge variety of gleefully disruptive gameplay challenges. " +
            "Winner of GAME OF THE YEAR at the Game Awards 2021.",
        LocalDate.of(2021, Month.MARCH, 26),
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTZbr5cPAmBx_h7waCKkI7x_1_LSJxiOKvV_Q&s",
        "https://store-images.s-microsoft.com/image/apps.40253.14488339386131194.84ca8b8a-582e-4d34-904e-8f1e60f71000.c3aaab37-0ce8-464b-85c1-4f42a74d9972?q=90&w=480&h=270"
    );
    games.add(gameTakesTwo);
    gamesDev2.add(gameTakesTwo);
  
    GameRepository gameRepository = new GameRepository(em);
    games.forEach(gameRepository::persist);
  
    /*  TAGS  */
  
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
  
    Tag tagIndie = new Tag("Indie", TagType.GENRE);
    tagIndie.setId(9999L);
  
    List<Tag> tagsPlatforms = new ArrayList<>(platforms.size());
    platforms.forEach(t -> tagsPlatforms.add(new Tag(t, TagType.PLATFORM)));
  
    List<Tag> tagsGenres = new ArrayList<>(platforms.size());
    tagsGenres.add(tagIndie);
    genres.forEach(t -> tagsGenres.add(new Tag(t, TagType.GENRE)));
  
  
    TagRepository tagRepository = new TagRepository(em);
    tagsPlatforms.forEach(tagRepository::persist);
    tagsGenres.forEach(tagRepository::persist);
  
    // platform tags
  
    for (int i = 0; i < gamesDev1.size(); i++) {
      gameRepository.addTag(developer1.getUser(), gamesDev1.get(i), tagsPlatforms.get((i) % tagsPlatforms.size()));
      gameRepository.addTag(developer1.getUser(), gamesDev1.get(i), tagsPlatforms.get((i + 1) % tagsPlatforms.size()));
      gameRepository.addTag(developer1.getUser(), gamesDev1.get(i), tagsPlatforms.get((i + 2) % tagsPlatforms.size()));
    }
    gameRepository.addTag(developer1.getUser(), gamesDev1.get(0), tagsPlatforms.get(4));
    gameRepository.addTag(developer1.getUser(), gamesDev1.get(1), tagsPlatforms.get(5));
  
    for (int i = 0; i < gamesDev2.size(); i++) {
      gameRepository.addTag(developer2.getUser(), gamesDev2.get(i), tagsPlatforms.get((i + 1) % tagsPlatforms.size()));
      gameRepository.addTag(developer2.getUser(), gamesDev2.get(i), tagsPlatforms.get((i + 3) % tagsPlatforms.size()));
      gameRepository.addTag(developer2.getUser(), gamesDev2.get(i), tagsPlatforms.get((i + 5) % tagsPlatforms.size()));
    }
    gameRepository.addTag(developer2.getUser(), gamesDev2.get(0), tagsPlatforms.get(4));
    gameRepository.addTag(developer2.getUser(), gamesDev2.get(1), tagsPlatforms.get(5));
  
    // genre tags
  
    for (int i = 0; i < gamesDev1.size(); i++) {
      gameRepository.addTag(developer1.getUser(), gamesDev1.get(i), tagsGenres.get(0));
      for (int j = i; j < 10; j += 2) {
        if (j == 0) continue;
        gameRepository.addTag(developer1.getUser(), gamesDev1.get(i), tagsGenres.get((j) % tagsGenres.size()));
      }
    }
  
    /* NEWS */
  
    NewsRepository newsRepository = new NewsRepository(em);
  
    // Developer 1
  
    newsRepository.persist(new News(
        "ANNOUNCING UNHOLY ALLIANCE.",
        "Summoned by blood and born in corruption, a wicked new ally can join the holy Lamb in LOCAL CO-OP! Crusade through dungeons, slay heretics, build your cult, and seek new powers together...",
        gameCult,
        developer1.getUser())
    );
  
    newsRepository.persist(
        new News(
            "CULT OF THE LAMB: THE FIRST VERSE",
            "The first-ever graphic novel inspired by CULT OF THE LAMB, is now available globally in stores!\n" +
                "\n" +
                "We’re thrilled to partner with OniPress to explore the world they create with our characters, " +
                "and witness the coming of the Lamb's first flock anew, together with you!",
            gameCult,
            developer1.getUser())
    );
  
    newsRepository.persist(
        new News(
            "Win Merch Through Our Base Builder Contest",
            "Dates: Monday 25th June-August 1st\n" +
                "\n" +
                "We're hosting a base builder contest over on our Discord! Build and share your dream cult layout, win prizes!\n" +
                "\n" +
                "\uD83E\uDD471st Place: Praise Lamb T-shirt in Black or White, Forneus Tote Bag, 3x Steam key for ANY Devolver Digital game on Steam\n" +
                "\uD83E\uDD482nd Place: Praise Lamb T-shirt in Black or White, 2x Steam key for ANY Devolver Digital game on Steam\n" +
                "\uD83E\uDD493rd Place: Praise Lamb T-shirt in Black or White 1x key for ANY Devolver Digital game on Steam!",
            gameCult,
            developer1.getUser())
    );
  
    newsRepository.persist(new News(
        "Lamb-Fi Beats!",
        "Vibe with The Lamb and enjoy the peaceful serenity of the Cult grounds, all from your own home. Now livestreaming 24/7 on Youtube!\n" +
            "\n" +
            "Our amazing artist Carles Dalmau, and animator JanAnimations joined forces to bring stunning visuals, paired with River Boy's soundtrack. " +
            "This was a huge amount of work from our team, so we hope you enjoy it - whether you're studying, vibing or sacrificing!",
        gameCult,
        developer1.getUser())
    );
  
  
    newsRepository.persist(new News(
        "1.6.5 patch notes",
        "After quickly fixing a few issues with 1.6.4, we are now on 1.6.5.\n" +
            "This one fixes a rare crash that could happen in the mines.\n" +
            "It should also help with some people who use mods that were having trouble loading their saves after 1.6.4.",
        gameStardew,
        developer1.getUser())
    );
  
    newsRepository.persist(new News(
        "A message to Stardew Valley's Chinese players",
        "After receiving feedback, we will be reverting the recent translation and font changes from 1.6.4\n" +
            "Dear Chinese players:\n" +
            "We've received a lot of feedback about the changes made in Stardew Valley version 1.6.4.\n" +
            "Therefore, we decided to revert the translation changes and fonts to version 1.6.3. (The smooth font will still be an option). This change will be made in the coming days.",
        gameStardew,
        developer1.getUser())
    );
  
    newsRepository.persist(new News(
        "Stardew Valley 1.6.6 Patch Notes",
        "This update makes a few balance and gameplay adjustments, a few significant translation changes, and fixes bugs\n" +
            "Hello everyone, here's the latest update. There's nothing too big in terms of gameplay additions or changes, but there are some important bug fixes, and translation adjustments.",
        gameStardew,
        developer1.getUser())
    );
  
    newsRepository.persist(new News(
        "1.6.7 -- Fixes a bug in 1.6.6",
        "1.6.7 change notes:\n" +
            "\n" +
            "-Fixes a bug preventing players from giving Pierre the Missing Stock List\n" +
            "-Fixed some machines and desert festival logic not working for Linux/MacOS players using the compatibility branch.",
        gameStardew,
        developer1.getUser())
    );
  
  
    newsRepository.persist(new News(
        "Zaumby Thursday",
        "Hey all, here again for the March Thursdoid. Lots of stuff from lots of different areas of the game this time, we do hope you enjoy.\n" +
            "\n" +
            "STRIKE A POSE\n" +
            "We’ve mentioned the fact that Build 42 will have a variety of readable materials that you can loot and inspect, " +
            "but we hadn’t shown them off in-game as quite frankly they didn’t look great. " +
            "They were crammed inside our existing Survival Guide UI, and it really wasn’t going to bat for us.",
        gamePz,
        developer1.getUser())
    );
  
    newsRepository.persist(new News(
        "D'ya Like Them GrappleZ",
        "Welcome all, to Knox Event proceedings on this fair spring morning. What’s up first?\n" +
            "LIFE’S A DRAG\n" +
            "\n" +
            "Something that, clearly, was fundamental to Build 41 was the new animations system – and thus far for Build 42 we haven’t mentioned any improvements to this.\n" +
            "\n" +
            "Importantly, it should be underlined that his work isn’t in the internal test build yet (and is certainly unconfirmed " +
            "as a part of 42 when it first enters an Unstable beta) but we’re far enough along now with work done in this area that " +
            "we’re comfortable to talk about ‘Zac’s GrappleTech’.",
        gamePz,
        developer1.getUser())
    );
  
    newsRepository.persist(new News(
        "Glowing Onez",
        "How do, all.\n" +
            " \n" +
            "CRAFTING CONSOLIDATION\n" +
            "At this point the main element of the Build 42 update that’s been holding stuff back has been the substantial crafting overhaul that’s been underway.",
        gamePz,
        developer1.getUser())
    );
  
    newsRepository.persist(new News(
        "The Biomic Man",
        "As discussed last time, in the interests of getting the 42 Unstable out of the door we are limiting the number of new craft disciplines that will be initially available " +
            "and then dripfeeding others (alongside polish, balance etc) in updates to the beta.\n" +
            "\n" +
            "We have done a bit of a stocktake on what we have done, and are aiming to have the following finished off for a first release.",
        gamePz,
        developer1.getUser())
    );
  
  
    newsRepository.persist(new News(
        "Cupdate - Hotfix 1.2.3",
        "Hotfix Patch Notes 1.2.3\n" +
            "\n" +
            "We hope everyone is having a swell time with Cuphead 1.2, enjoying the chance to play as Mugman or exploring the Inkwell Isles in a brand new language.",
        gameCuphead,
        developer1.getUser())
    );
  
    newsRepository.persist(new News(
        "Prepare for Cuphead DLC with Cupdate 1.3.2!",
        "Well hello there!\n" +
            "\n" +
            "It’s surreal to be able to say this, but the time is here at last…The Delicious Last Course expansion launches tomorrow. " +
            "We cannot wait for the wonderful Cuphead community on Steam to set sail for this new adventure, " +
            "experience the story of brand new playable character Ms. Chalice, and take on some of the biggest and wildest boss battles we’ve ever created!",
        gameCuphead,
        developer1.getUser())
    );
  
    newsRepository.persist(new News(
        "Improve your DLC…with Patch 1.3.3 !!",
        "Salutations folks!!\n" +
            "\n" +
            "It has been a little over 2 weeks since the launch of our Cuphead expansion The Delicious Last Course, " +
            "and we come bearing the tastiest treat of them all…bug fixes!! In seriousness, we want to thank our wonderful " +
            "Steam Community for the insightful comments and Community threads chronicling your early experiences with DLC.",
        gameCuphead,
        developer1.getUser())
    );
  
    newsRepository.persist(new News(
        "Swap Your Title Screen…with patch 1.3.4!!",
        "Hi there pals!\n" +
            "Just a quick update for everyone this time — we’ve now pushed live a small quality of life patch that sneaks in a few additional fixes for our " +
            "Delicious Last Course expansion. Foremost among them, we’ve added in a feature that has been highly requested since the launch of DLC…the ability " +
            "to swap back and forth between the new title screen and the original Cuphead title screen!",
        gameCuphead,
        developer1.getUser())
    );
  
  
    newsRepository.persist(new News(
        "The Challenge Moons Patch - Version 47",
        "Hello employees, I hope you had a good Christmas. Now get back to work!!!\n" +
            "I've been taking a break, catching up on movies and writing and drawing, so this update isn't too crazy but has lots of little things. " +
            "In Version 50 I hope to add lots of new creatures and map variation.",
        gameLethal,
        developer1.getUser())
    );
  
    newsRepository.persist(new News(
        "Version 50 - The Hopping Update",
        "Hi, it's been a long time, but at long last I have another lethal update to bring you! " +
            "I hope you'll forgive me for suggesting in a previous post here that lethal updates would be somewhat regular and predictable--that was a bit naïve of me. " +
            "Sorry, I'm just not working like I have a quota, because that would be ironic.",
        gameLethal,
        developer1.getUser())
    );
  
    newsRepository.persist(new News(
        "Version 55 now available in the beta branch!",
        "Hello, you can now test Version 55 in the public beta branch! " +
            "Try out the Company Cruiser--the most safe and luxurious method of transportation. " +
            "I'll be working on improvements and fixes over the next few days until I feel the changes do not harm the experience, " +
            "then I will release it officially. I hope you have fun!",
        gameLethal,
        developer1.getUser())
    );
  
    newsRepository.persist(new News(
        "Version 55 - The Cruising Update",
        "Hello, Version 55 is here! This update is mainly just the Company Cruiser, an epic luxury transportation vehicle, " +
            "capable of transporting an unimaginable amount of scrap all at once.",
        gameLethal,
        developer1.getUser())
    );
  
  
    // Developer 2
  
    newsRepository.persist(new News(
        "RELEASE 21 JAN 2023",
        "Are you ready? The game is coming out very soon!\n",
        gameCats,
        developer2.getUser())
    );
  
    newsRepository.persist(new News(
        "\uD83E\uDD16\uD83D\uDE3B 100 ROBO CATS IS OUT! \uD83E\uDDBE\uD83D\uDC08",
        "\uD83D\uDC08 Meow! Our new game is already available! Hurry up to get a real Robo vibe with your furry friends! \uD83C\uDF89",
        gameCats,
        developer2.getUser())
    );
  
  
    newsRepository.persist(new News(
        "\"Host of Horrors\" Content Update Available Now!",
        "New horrors await! As more rifts appear across the Constant, monsters have started rising from the dead, becoming vessels for unearthly apparitions. " +
            "You'd better not let your guard down...",
        gameStarve,
        developer2.getUser())
    );
  
    newsRepository.persist(new News(
        "Scrappy Scavengers Update Now Available!",
        "One person's junk is another's armament. An old foe from below returns, hungry for a new source of power. Have you enough courage to challenge him for it?",
        gameStarve,
        developer2.getUser())
    );
  
    newsRepository.persist(new News(
        "Hotfix 617969",
        "Fixed a crash related to Night Lights loading in a world.",
        gameStarve,
        developer2.getUser())
    );
  
  
    newsRepository.persist(new News(
        "The List of Upcoming Features",
        "The future is brighter than ever! See what new features will be available in Potion Craft in the upcoming updates!",
        gamePotion,
        developer2.getUser())
    );
  
    newsRepository.persist(new News(
        "Quality-of-Life & Optimization Update v1.1 is here!",
        "When you hover over ingredients while browsing a merchant’s goods you now see the number of those ingredients in your inventory in the tooltip. " +
            "Now you’ll know for sure when you are out of Poopshrooms!",
        gamePotion,
        developer2.getUser())
    );
  
    newsRepository.persist(new News(
        "Potion Craft is 55% OFF!",
        "Get Potion Craft 55% off during the Steam Winter Sale – Offer ends January 4th!",
        gamePotion,
        developer2.getUser())
    );
  
  
    newsRepository.persist(new News(
        "A wild milestone achievement!",
        "It Takes Two is celebrating a huge milestone: 16 million units! Thank you to all of our fans – your support means everything to us.",
        gameTakesTwo,
        developer2.getUser())
    );
  
    newsRepository.persist(new News(
        "Save on It Takes Two",
        "Get 65%* off until September 25 (Steam).",
        gameTakesTwo,
        developer2.getUser())
    );
  
    newsRepository.persist(new News(
        "A CRAZY JOURNEY - 10 MILLION UNITS SOLD!",
        "It’s so amazing to see all the support for It Takes Two. Thank you to all the fans and community for going on this magical journey together with us!",
        gameTakesTwo,
        developer2.getUser())
    );
    
    
    /*  // TODO(Hollow Knight)
    newsRepository.persist(new News(
        "",
        "",
        gamePotion,
        developer2.getUser())
    );
    */
  
    /* REVIEWS */
  
    ReviewRepository reviewRepository = new ReviewRepository(em);
    List<User> reviewers = getReviewUsers();
    int i = 2;
  
    List<String> reviewsCult = List.of(
        "I put my coworkers in the game and bully the ones i dont like",
        "Excellent, 10/10, cried when I sacrificed my fav and longest follower of 102 days",
        "you can fish in this game!!",
        "If Hades and Animal Crossing had a baby this would be it. Fantastic rogue like elements, great farming system, and adorable characters/plot. Highly recommend this game!"
    );
  
    for (String r : reviewsCult) {
      reviewRepository.addReview(new Review(r), reviewers.get(i++), gameCult);
      i = i % reviewers.size();
    }
  
    List<String> reviewsStardew = List.of(
        "still waiting for the update where i can marry robin",
        "This game is so good if youre either a teenage girl (i am) or a grown ass depressed man",
        "has cats 10/10"
    );
  
    for (String r : reviewsStardew) {
      reviewRepository.addReview(new Review(r), reviewers.get(i++), gameStardew);
      i = i % reviewers.size();
    }
  
    List<String> reviewsPz = List.of(
        "If you watch CallMeKevin, you know this is a great game.",
        "it's aight",
        "pros: most realistic zombie survival game\ncons: most realistic zombie survival game"
    );
  
    for (String r : reviewsPz) {
      reviewRepository.addReview(new Review(r), reviewers.get(i++), gamePz);
      i = i % reviewers.size();
    }
  
    List<String> reviewsCuphead = List.of(
        "i'd smash everyone in this game.",
        "Cuphead needs an online play mode PLEASE",
        "If u want mental suffer, try this!!! \uD83D\uDDE3\uD83D\uDDE3\uD83D\uDD25\uD83D\uDD25\uD83D\uDD25\uD83D\uDD25",
        "love it and hate it at the same time"
    );
  
    for (String r : reviewsCuphead) {
      reviewRepository.addReview(new Review(r), reviewers.get(i++), gameCuphead);
      i = i % reviewers.size();
    }
  
    List<String> reviewsLethal = List.of(
        "Great game but please put DirectX 10 support so that my friend and others with weak computers can play too",
        "I DONT CARE WHATS IN THAT BUILDING, ARTHUR, WE NEED MONEY",
        "The Average Amazon worker Experience."
    );
  
    for (String r : reviewsLethal) {
      reviewRepository.addReview(new Review(r), reviewers.get(i++), gameLethal);
      i = i % reviewers.size();
    }
  
    List<String> reviewsPotion = List.of(
        "Medieval Drugdealer Simulator is pretty good. Bought it on sale, but I believe it's worth the full price. Good game.",
        "Fun puzzle game. Gets a bit repetitive, but not too bad for the length of the game. Recommend to buy if on sale.",
        "Super cute and whimsical little game about witchy things.",
        "I’m not sure about recommending it at full price, but it’s worth a spot in your collection."
    );
  
    for (String r : reviewsPotion) {
      reviewRepository.addReview(new Review(r), reviewers.get(i++), gamePotion);
      i = i % reviewers.size();
    }
  
    List<String> reviewsStarve = List.of(
        "Don't starve and you'll win.",
        "i frickin died of hungary.",
        "fun game",
        "i liked the parts where i wasn't dying of starvation but did not like the parts where i was dying of everything else"
    );
  
    for (String r : reviewsStarve) {
      reviewRepository.addReview(new Review(r), reviewers.get(i++), gameStarve);
      i = i % reviewers.size();
    }
  
    List<String> reviewsCats = List.of(
        "A fine, free Where's Wally/Waldo style game.",
        "Took me 6 minutes to finish it.",
        "Some cats aren't drawn to be obviously a cat and can leave you stumped, but it has a hint system.",
        "It's banking for your money but don't demand your wallet at gunpoint, now that's cat-pitalism",
        "i lobe cats."
    );
  
    for (String r : reviewsCats) {
      reviewRepository.addReview(new Review(r), reviewers.get(i++), gameCats);
      i = i % reviewers.size();
    }
  
    List<String> reviewsTakesTwo = List.of(
        "You see EA, when you actually let talented people with good ideas do what they want, you get games like this. This is actually incredible, we need more games like this one.",
        "was a really enjoyable experience to play w/ my partner! <3 a lot more content in the game than it seems!!",
        "One of the best co-op games ever made, each stage has different game mechanics which makes the experience of each stage different. The puzzles are not repetitive and easy to understand. Very recommended for playing with friends or family",
        "The best game to team up with your soulmate<3",
        "we broke up"
    );
  
    for (String r : reviewsTakesTwo) {
      reviewRepository.addReview(new Review(r), reviewers.get(i++), gameTakesTwo);
      i = i % reviewers.size();
    }
  
    /* FRIENDS */
  
    User hasFriends = new User(
        "IHaveFriends",
        "gamelib.test+IHaveFriends@gmail.com",
        "1234",
        Rol.USER
    );
    
    userRepository.persist(hasFriends);
    
    int n = reviewers.size() / 2 + 1;
    
    for (int j = 0; j < n; j++) {
      hasFriends.addFriend(reviewers.get(j));
    }
    
    for (int j = n; j < reviewers.size(); j++) {
      reviewers.get(j).sendFriendRequest(hasFriends);
    }
    
    userRepository.persist(hasFriends);
    //reviewers.forEach(userRepository::persist);
    
    /* END */
    em.close();
  }
  
  
  private List<User> getReviewUsers() {
    List<User> users = List.of(
        new User(
            "username321",
            "gamelib.test+username321@gmail.com",
            "1234",
            Rol.USER
        ),
        new User(
            "username685",
            "gamelib.test+username685@gmail.com",
            "1234",
            Rol.USER
        ),
        new User(
            "username193",
            "gamelib.test+username193@gmail.com",
            "1234",
            Rol.USER
        ),
        new User(
            "username954",
            "gamelib.test+username954@gmail.com",
            "1234",
            Rol.USER
        ),
        new User(
            "username835",
            "gamelib.test+username835@gmail.com",
            "1234",
            Rol.USER
        ),
        new User(
            "username268",
            "gamelib.test+username268@gmail.com",
            "1234",
            Rol.USER
        ),
        new User(
            "username894",
            "gamelib.test+username894@gmail.com",
            "1234",
            Rol.USER
        )
    );
    
    List<String> pfp = List.of(
        "https://wallpapers.com/images/featured/cool-profile-picture-87h46gcobjl5e4xu.jpg",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ7KsJjbFi6gMYm3V8LbyFAF3oWXirYhd_Urg&s",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR3HTgKkm-tBcNbiLG5eCL12nOngY_fEta2BA&s",
        "https://images.pexels.com/photos/771742/pexels-photo-771742.jpeg",
        "https://play-lh.googleusercontent.com/HnzbI7urJlB6V26dtKiawYoBrH4iR5DAAk4KqNZzIa0NRWQukskR6aX7IrV55AULKIgA=w240-h480-rw",
        "https://media.newyorker.com/photos/62c4511e47222e61f46c2daa/master/w_2560%2Cc_limit/shouts-animals-watch-baby-hemingway.jpg",
        "https://i.pinimg.com/originals/d5/72/ed/d572ed01adbaf8baf3a39b1db2c9b1c5.png"
    );
    
    for (int i = 0; i < users.size(); i++) {
      users.get(i).setPfp(pfp.get(i));
    }
    
    EntityManager em = factory.createEntityManager();
    UserRepository userRepository = new UserRepository(em);
    List<User> usersSaved = new ArrayList<>(users.size());
    users.forEach(u -> usersSaved.add(userRepository.persist(u)));
    
    return usersSaved;
  }
}
