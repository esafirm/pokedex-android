package id.devfest.pokedex.di.component

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import id.devfest.pokedex.App
import id.devfest.pokedex.di.ActivityBuilder
import id.devfest.pokedex.di.module.AppModule
import id.devfest.pokedex.di.module.NetModule
import javax.inject.Singleton

/**
 * Created by fathonyfath on 04/02/18.
 */
@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AppModule::class,
    NetModule::class,
    ActivityBuilder::class
])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun netModule(netModule: NetModule): Builder
        fun build(): AppComponent
    }

    fun inject(app: App)
}