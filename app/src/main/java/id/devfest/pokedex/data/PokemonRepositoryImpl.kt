package id.devfest.pokedex.data

import id.devfest.pokedex.data.api.PokeAPI
import id.devfest.pokedex.data.repository.PokemonRepository
import id.devfest.pokedex.data.storage.FavoritePokemonStorage
import id.devfest.pokedex.data.storage.PokemonStorage
import id.devfest.pokedex.model.Detail
import id.devfest.pokedex.model.Pokemon
import id.devfest.pokedex.model.Profile
import id.devfest.pokedex.utils.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Created by fathonyfath on 04/02/18.
 */

class PokemonRepositoryImpl constructor(
    private val pokeAPI: PokeAPI,
    private val pokemonStorage: PokemonStorage,
    private val favoritePokemonStorage: FavoritePokemonStorage,
    private val pokemonImageGenerator: PokemonImageGenerator
) : PokemonRepository {

    private val pokemonDetailRequestQueue: MutableSet<Int> = mutableSetOf()

    override fun listenToPokemonList(): Observable<List<Pokemon>> {
        return pokemonStorage
            .listenToPokemonMap()
            .map { it.map { it.value } }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }

    override fun listenToPokemonId(pokemonId: Int): Observable<Pokemon> {
        return pokemonStorage
            .listenToPokemonId(pokemonId)
            .map { it.second }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }

    override fun fetchPokemonDetail(pokemonId: Int): Single<Either<Throwable, Boolean>> {
        if (pokemonStorage.getPokemon(pokemonId)?.detail == null) {
            pokemonDetailRequestQueue.add(pokemonId)
            return pokeAPI.getPokemonDetail(pokemonId)
                .map {
                    val abilities = it.abilities.map { it.abilityDetail.name }.map {
                        it.removeDash().capitalizeFirstLetter()
                    }
                    val types = it.types.map { it.typeDetail.name }.map {
                        it.removeDash().capitalizeFirstLetter()
                    }

                    val stats = mutableMapOf<String, Int>()

                    it.stats.forEach {
                        stats[it.statDetail.name.removeDash().capitalizeFirstLetter()] = it.baseStat
                    }

                    val detail = Detail(types, abilities,
                        Profile(it.weight, it.height, it.baseExperience), stats)

                    val pokemon = Pokemon(
                        it.id,
                        it.name.removeDash().capitalizeFirstLetter(),
                        pokemonImageGenerator.getImageUrl(it.id), detail)

                    return@map Pair(pokemonId, pokemon)
                }
                .doOnSuccess { pokemonStorage.putPokemon(it.second) }
                .doOnSuccess { pokemonDetailRequestQueue.remove(pokemonId) }
                .doOnError { pokemonDetailRequestQueue.remove(pokemonId) }
                .map<Either<Throwable, Boolean>> { Either.Right(true) }
                .onErrorReturn { Either.Left(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
        } else {
            return Single.just(Either.Right(true))
        }
    }

    override fun fetchMorePokemon(offsetPokemonId: Int): Single<Either<Throwable, Boolean>> {
        return pokeAPI.getPokemonList(offsetPokemonId)
            .map {
                val pokemonList = arrayListOf<Pokemon>()
                for (pokemon in it.results) {
                    val pokemonId = pokemon.url.getIdFromURI()
                    pokemonId.let {
                        pokemonList.add(
                            Pokemon(
                                it,
                                pokemon.name.removeDash().capitalizeFirstLetter(),
                                pokemonImageGenerator.getImageUrl(it)
                            )
                        )
                    }
                }
                return@map pokemonList.toList()
            }
            .doOnSuccess { it.forEach { item -> pokemonStorage.putPokemon(item) } }
            .map { it.isNotEmpty() }
            .map<Either<Throwable, Boolean>> { Either.Right(it) }
            .onErrorReturn { Either.Left(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }

    /* --------------------------------------------------- */
    /* > Favorite */
    /* --------------------------------------------------- */

    override fun getFavoritePokemon() = favoritePokemonStorage.getFavoritePokemon()

    override fun setFavoritePokemon(pokemon: Pokemon): Single<Unit> {
        return Single.fromCallable {
            favoritePokemonStorage.setFavoritePokemon(pokemon)
        }
    }
}