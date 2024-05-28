package entities;

public class ErrorMessages {

    public static String usernameNotFound(String username) {
        return "There is no user " + username + "!";
    }

    public static String usernameAlreadyInUse(String username) {
        return "Username "+ username + " is already in use!";
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

    public static String informationIncorrectFormat(String info) {
        return info + " is not formatted correctly!";
    }

    public static String userNotAllowedToPerformAction() {
        return "You are not allowed to perform this action!";
    }

    public static String userMustBeLoggedIn() {
        return "You must be logged in to perform this action!";
    }

    public static String userMustBeDeveloper() {
        return "You must be a Developer to perform this action!";
    }
}
