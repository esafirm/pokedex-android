package id.devfest.pokedex.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import id.devfest.pokedex.data.PokemonRepositoryImpl
import id.devfest.pokedex.data.api.PokeAPI
import id.devfest.pokedex.data.repository.PokemonRepository
import id.devfest.pokedex.data.storage.PokemonStorage
import id.devfest.pokedex.di.ViewModelBuilder
import id.devfest.pokedex.utils.PokemonImageGenerator
import javax.inject.Singleton

/**
 * Created by fathonyfath on 04/02/18.
 */
@Module(includes = [
    ViewModelBuilder::class
])
open class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application

    @Provides
    @Singleton
    fun providePokemonRepository(pokeAPI: PokeAPI,
                                 pokemonStorage: PokemonStorage,
                                 pokemonImageGenerator: PokemonImageGenerator): PokemonRepository =
            PokemonRepositoryImpl(pokeAPI, pokemonStorage, pokemonImageGenerator)

}