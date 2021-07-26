package com.example.datepicker;

import android.os.Environment;

import java.io.File;

public interface Constants {

    public static final String IP_ADDRESS = "http://app.chowis.com:8080";
    public static final String IMAGE_URL = IP_ADDRESS + "/upload/uploadImage.do";

    public static final String PROJECTNAME = "DermoPico";
    public static final String PROJECTNAME2 = "DermoPico";

    public static final String DATABASENAME = "picoskin.db";

    public static final String PICO_PATH = Environment.getExternalStorageDirectory() + File.separator + PROJECTNAME;
    public static final String PICO_TEMP_PATH = PICO_PATH + File.separator + "temp";
    public static final String PICO_DATA_PATH = PICO_PATH + File.separator + "db";
    public static final String IMAGE_PATH = PICO_PATH + File.separator + "images";

    public static final String IMAGE_TEMP_PATH = IMAGE_PATH + File.separator + "temp";
    public static final String IMAGE_CLIENT_PATH = IMAGE_PATH + File.separator + "clients";

    public static final String PREF_OPTIC_NUMBER = "PREF_SERIAL_NUMBER";
    public static final String PREF_NONE_DISPLAY_CAPTURE_CAUTION = "PREF_NONE_DISPLAY_CAPTURE_CAUTION";
    public static final String PREF_CHECK_VERSION_RELEASE = "PREF_CHECK_VERSION_RELEASE";

    // Preference constants
    public static final String PREF_CLIENT_SEQUENCE = "PREF_CLIENT_SEQUENCE";
    public static final String PREF_CLIENT_ID = "PREF_CLIENT_ID";
    public static final String PREF_CLIENT_NAME = "PREF_CLIENT_NAME";
    public static final String PREF_CLIENT_SUR_NAME = "PREF_CLIENT_SUR_NAME";
    public static final String PREF_CLIENT_TELEPHONE = "PREF_CLIENT_TELEPHONE";
    public static final String PREF_CLIENT_MOBILE = "PREF_CLIENT_MOBILE";
    public static final String PREF_CLIENT_EMAIL = "PREF_CLIENT_EMAIL";
    public static final String PREF_CLIENT_GENDER = "PREF_CLIENT_GENDER";
    public static final String PREF_CLIENT_BIRTHDATE = "PREF_CLIENT_BIRTHDATE";
    public static final String PREF_CLIENT_AGE = "PREF_CLIENT_AGE";
    public static final String PREF_CLIENT_ADDRESS = "PREF_CLIENT_ADDRESS";
    public static final String PREF_CLIENT_CITY = "PREF_CLIENT_CITY";
    public static final String PREF_CLIENT_COUNTRY = "PREF_CLIENT_COUNTRY";
    public static final String PREF_CLIENT_POSTCODE = "PREF_CLIENT_POSTCODE";
    public static final String PREF_CLIENT_MEMO = "PREF_CLIENT_MEMO";
    public static final String PREF_CLIENT_SKINTYPE = "PREF_CLIENT_SKINTYPE";
    public static final String PREF_CLIENT_SKINCOLOR = "PREF_CLIENT_SKINCOLOR";
    public static final String PREF_CLIENT_ZC = "PREF_CLIENT_ZC";
    public static final String PREF_CLIENT_DAY = "PREF_CLIENT_DAY";
    public static final String PREF_CLIENT_MONTH = "PREF_CLIENT_MONTH";
    public static final String PREF_CLIENT_YEAR = "PREF_CLIENT_YEAR";
    public static final String PREF_CLIENT_NOTES = "PREF_CLIENT_NOTES";

    public static final String PREF_CLIENT_REGISTRATION = "PREF_CLIENT_REGISTRATION";

    public static final String PREF_SHOW_SEND_TO_CHOWIS_FILE_COUNT = "PREF_SHOW_SEND_TO_CHOWIS_FILE_COUNT";

    public static final String COMPANY_TYPE = "COMPANY_TYPE";
    public static final String PREF_ANALYSIS_SEQ = "ANALYSIS_SEQ";

    public static final String PREF_DIAGNOSIS_IMAGE_SEBUM_T = "PREF_DIAGNOSIS_IMAGE_SEBUM_T";
    public static final String PREF_DIAGNOSIS_IMAGE_SEBUM_U = "PREF_DIAGNOSIS_IMAGE_SEBUM_U";
    public static final String PREF_DIAGNOSIS_VALUE_PORES = "PREF_DIAGNOSIS_VALUE_PORES";
    public static final String PREF_DIAGNOSIS_VALUE_SPOTS = "PREF_DIAGNOSIS_VALUE_SPOTS";
    public static final String PREF_DIAGNOSIS_VALUE_WRINKLE = "PREF_DIAGNOSIS_VALUE_WRINKLE";
    public static final String PREF_DIAGNOSIS_VALUE_ACNE = "PREF_DIAGNOSIS_VALUE_ACNE";
    public static final String PREF_DIAGNOSIS_VALUE_KERATIN = "PREF_DIAGNOSIS_VALUE_KERATIN";
    public static final String PREF_DIAGNOSIS_VALUE_COMMENTS = "PREF_DIAGNOSIS_VALUE_COMMENTS";

    public static final String PREF_DEVICE_BONED_MAC_ADDRESS = "PREF_DEVICE_BONED_MAC_ADDRESS";


    public static final int SEBUM = 0;
    public static final int PORE = 1;
    public static final int WRINKLE = 2;
    public static final int ACNE = 3;

    public static final String MODE_MOISTURE = "MODE_MOISTURE";
    public static final String MODE_PORE = "MODE_PORE";
    public static final String MODE_PORPHYRIN = "MODE_PORPHYRIN";
    public static final String MODE_SEBUM = "MODE_SEBUM";
    public static final String MODE_WRINKLE = "MODE_WRINKLE";


    public static final String MODE_BLACKHEAD = "MODE_BLACKHEAD";


    public static final String LIST_PORES = "LIST_PORES";
    public static final String LIST_PORPHYRINS = "LIST_PORPHYRINS";
    public static final String LIST_SEBUMS = "LIST_SEBUMS";
    public static final String LIST_WRINKLES = "LIST_WRINKLES";
    public static final String LIST_BLACKHEAD = "LIST_BLACKHEAD";
    public static final String USER_LOCATION = "USER_LOCATION";

    // 인종별 상수
    public static final String AS = "AS";
    public static final String AE = "AE";
    public static final String CA = "CA";
    public static final String DS = "DS";
    public static final String HI = "HI";
    public static final String F1 = "F1";
    public static final String F2 = "F2";
    public static final String F3 = "F3";
    public static final String F4 = "F4";
    public static final String F5 = "F5";
    public static final String F6 = "F6";

    public static final String PREF_HISTORY_MODE = "PREF_HISTORY_MODE";
    public static final String PREF_HISTORY_DIAGNOSIS_SEQ = "PREF_HISTORY_DIAGNOSIS_SEQ";
    // preference - temp
    public static final String USER_LAST_ID = "USER_LAST_ID";
    public static final String USER_GENDER = "USER_GENDER";
    public static final String USER_AGE = "USER_AGE";
    public static final String USER_SKIN_TYPE = "USER_SKIN_TYPE";


    public static final String GENDER_FEMALE = "F";
    public static final String GENDER_MALE = "M";


    // 고객사 구분
    public static final int isDermoPico_Chowis = 0;
    public static final int isDermoPico_Manzhiyan = 1;

    public int RELEASE_COMPANY = isDermoPico_Chowis;
}