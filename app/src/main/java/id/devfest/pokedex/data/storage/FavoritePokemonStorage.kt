package id.devfest.pokedex.data.storage

import id.devfest.pokedex.model.Pokemon
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class FavoritePokemonStorage @Inject constructor(
    private val pokemonPref: SimplePokemonPreference
) {
    private val favoriteSubject = BehaviorSubject.create<Pokemon>()

    init {
        fetchFavorite()
    }

    private fun fetchFavorite() {
        val initialFavorite = pokemonPref.getFavoritePokemon()
        if (initialFavorite != null) {
            favoriteSubject.onNext(initialFavorite)
        }
    }

    fun getFavoritePokemon(): Observable<Pokemon> = favoriteSubject

    fun setFavoritePokemon(pokemon: Pokemon) {
        pokemonPref.setFavoritePokemon(pokemon)
        favoriteSubject.onNext(pokemon)
    }
}