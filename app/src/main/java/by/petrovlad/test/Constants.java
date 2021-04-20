package by.petrovlad.test;

import com.google.maps.android.geometry.Point;

import java.util.ArrayList;

public final class Constants {
    public static final String KITTENS_REFERENCE = "Kittens";
    public static final String KITTEN_LOCATIONS_REFERENCE = "kitten_locations";

    public static final String PASSWORD_EXTRA = "Password";
    public static final String EMAIL_EXTRA = "Login";
    public static final String UPN_SUFFIX = "@kittens.org";
    public static final String KITTEN_NAME_EXTRA = "KittenName";
    public static final String KITTEN_TAIL_LENGTH_EXTRA = "KittenTailLength";
    public static final String KITTEN_EYES_COLOR_EXTRA = "KittenEyesColor";
    public static final String KITTEN_HEIGHT_EXTRA = "KittenHeight";
    public static final String UID_EXTRA = "uid";
    public static final String KITTEN_HEADERS = "KittenHeaders";
    public static final int PICK_IMAGE_REQUEST = 1337;
    public static final int PICK_VIDEO_REQUEST = 228;
    public static final String FIREBASE_IMAGES_REFERENCE = "images";
    public static final String FIREBASE_VIDEOS_REFERENCE = "videos";
    public static final String IMAGE_URL_EXTRA = "url";
    public static final String IMAGE_NAME_EXTRA = "name";
    public static final int AVATAR_WIDTH = 150;
    public static final int AVATAR_HEIGHT = 150;
    public static final String MAPS_API_KEY = "AIzaSyCMUpCYD205d17eeretXZZGUAzIS5Ulye0";
    public static final Point MINSK_POINT = new Point(53.9, 27.6);
    public static final String ENGLISH_LANGUAGE = "en";
    public static final String RUSSIAN_LANGUAGE = "ru";
    public static final String[] LANGUAGES = {"en", "ru"};
    public static final Integer MARKER_CLICKED_TAG = 1;
    public static final Integer MARKER_UNCLICKED_TAG = 0;

    private Constants() {
        throw new AssertionError();
    }
}
