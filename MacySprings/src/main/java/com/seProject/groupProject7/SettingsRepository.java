package com.seProject.groupProject7;

import org.springframework.data.repository.CrudRepository;

import com.seProject.groupProject7.User;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface SettingsRepository extends CrudRepository<ServerSetting, Integer> {

    /*

        INITIALIZE THIS IN DATABASE: create table seproject7.server_setting(name TEXT, value TEXT);

     */
}


