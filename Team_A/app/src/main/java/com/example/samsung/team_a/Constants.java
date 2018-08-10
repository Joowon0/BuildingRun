/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.samsung.team_a;

/**
 * Defines several constants used between {@link BluetoothChatService} and the UI.
 */
public interface Constants {

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    //Sign Up
    public static final int SU_SUCCESS = 1;
    public static final int SU_DUPLICATED = 2;
    public static final int SU_NONCE_EXIT = 3;

    //Sign In
    public static final int SI_SUCCESS = 1;
    public static final int SI_NO_SUCH_EMAIL = 2;
    public static final int SI_WRONG_PASSWORD = 3;
    public static final int SI_NONCE_EXIST = 4;

    //Password Change
    public static final int PC_CORRECT_PASSWORD = 1;
    public static final int PC_WRONG_PASSWORD = 2;
    public static final int PC_NO_SUCH_ACCOUNT = 3;

    //Reset Password
    public static final int RP_EMAIL_EXIST = 1;
    public static final int RP_EMAIL_NOT_EXIST = 2;

    //Id cancellation
    public static final int IC_EMAIL_EXIST = 1;
    public static final int IC_EMAIL_NOT_EXIST = 2;
    public static final int IC_NO_SUCH_ACCOUNT = 3;

    //bluetooth connect first or not
    public static final int BT_REGISTER = 1;
    public static final int BT_USING = 2;

}
