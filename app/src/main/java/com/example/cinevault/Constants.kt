package com.example.cinevault

object Constants {
    // Emulator:
    const val BASE_URL = "http://10.81.20.111:3000/"

    // If you test on a real phone with:
    // adb reverse tcp:3000 tcp:3000
    // then change the line above to:
    // const val BASE_URL = "http://127.0.0.1:3000/"
}