package com.example.todo

import android.app.Application

// It's part of your app's initialization process
class TodoApplication:Application() {
    //override This method is called when
    // your application is first created, and it's a suitable place to perform one-time setup tasks for your app.
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}