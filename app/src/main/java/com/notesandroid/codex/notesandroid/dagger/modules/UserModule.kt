package com.notesandroid.codex.notesandroid.dagger.modules

import com.notesandroid.codex.notesandroid.Essences.User
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Class for inject that need for User like as User
 */
@Module
class UserModule{

    /**
     * function that provide us only one instance of User that contain jwt, information about user and etc.
     * @return instance of User class
     */
    @Provides
    @Singleton
    fun getUserAccount() = User()
}