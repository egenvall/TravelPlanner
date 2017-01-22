package com.egenvall.travelplanner.common.injection.module


import com.egenvall.travelplanner.TravelPlanner
import com.egenvall.travelplanner.persistance.IRealmInteractor
import com.egenvall.travelplanner.persistance.RealmInteractorImpl
import dagger.Module
import dagger.Provides
import io.realm.Realm
import io.realm.RealmConfiguration
import javax.inject.Singleton

@Module
class RealmModule(val app : TravelPlanner) {
    @Singleton
    @Provides
    internal fun provideRealmConfiguration() : RealmConfiguration
    {
        Realm.init(app)
        return RealmConfiguration
                .Builder().deleteRealmIfMigrationNeeded().build()
    }

    @Singleton
    @Provides
    internal fun provideRealm(realmConfiguration: RealmConfiguration) : Realm
    {
        return Realm.getInstance(realmConfiguration)
    }
    @Singleton
    @Provides
    internal fun provideInteractor(realm : Realm) : IRealmInteractor{
        return RealmInteractorImpl(realm)
    }
}