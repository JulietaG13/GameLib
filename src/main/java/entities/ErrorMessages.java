package entities;

public class ErrorMessages {

    public static String usernameNotFound(String username) {
        return "There is no user " + username + "!";
    }
    
    public static String informationNotProvided(String info) {
        return "You must provide a " + info + "!";
    }
    
    public static String informationNotFound(String info) {
        return info + " not found!";
    }
    
    public static String informationNotNumber(String info) {
        return info + " must be a number!";
    }
    
    public static String informationNotBoolean(String info) {
        return info + " must be true or false!";
    }
}
