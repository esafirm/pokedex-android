package id.devfest.pokedex

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.devfest.pokedex.data.repository.PokemonRepository
import id.devfest.pokedex.model.Pokemon
import id.devfest.pokedex.utils.asMutable
import id.devfest.pokedex.utils.toLiveData
import io.reactivex.BackpressureStrategy
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by fathonyfath on 04/02/18.
 */
class MainViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository
) : ViewModel() {

    private val _pokemonList: MutableLiveData<List<Pokemon>> = MutableLiveData()
    private val _loadMoreResult: MutableLiveData<Pair<Result, Boolean>> = MutableLiveData()

    private val _fetchDetailResult: MutableLiveData<Result> = MutableLiveData()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    val pokemonList: LiveData<List<Pokemon>>
        get() = _pokemonList

    val loadMoreResult: LiveData<Pair<Result, Boolean>>
        get() = _loadMoreResult

    val fetchDetailResult: LiveData<Result>
        get() = _fetchDetailResult

    val favoritePokemon: LiveData<kotlin.Result<Pokemon>> = MutableLiveData()

    init {
        compositeDisposable.add(
            pokemonRepository.listenToPokemonList().subscribe {
                _pokemonList.postValue(it)
            }
        )

        compositeDisposable.add(
            pokemonRepository.getFavoritePokemon().subscribe {
                favoritePokemon.asMutable().postValue(kotlin.Result.success(it))
            }
        )
    }

    fun observePokemonWithId(pokemonId: Int): LiveData<Pokemon> {
        return pokemonRepository.listenToPokemonId(pokemonId)
            .toFlowable(BackpressureStrategy.BUFFER)
            .toLiveData()
    }

    fun triggerLoadMore(offset: Int) {
        _loadMoreResult.value = null
        compositeDisposable.add(
            pokemonRepository.fetchMorePokemon(offset).subscribe({
                it.either({
                    _loadMoreResult.postValue(Result.Error to true)
                }, {
                    _loadMoreResult.postValue(Result.Success to it)
                })
            }, { })
        )
    }

    fun fetchPokemonDetails(pokemonId: Int) {
        _fetchDetailResult.value = null
        compositeDisposable.add(
            pokemonRepository.fetchPokemonDetail(pokemonId).subscribe({
                it.either({
                    _fetchDetailResult.postValue(Result.Error)
                }, {
                    _fetchDetailResult.postValue(Result.Success)
                })
            }, { })
        )
    }

    fun setFavoritePokemon(pokemon: Pokemon) {
        compositeDisposable.add(
            pokemonRepository.setFavoritePokemon(pokemon).subscribe { _, e ->
                if (e != null) {
                    val liveData = favoritePokemon.asMutable()
                    liveData.postValue(kotlin.Result.failure(e))
                }
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    sealed class Result {
        object Error : Result()
        object Success : Result()
    }
}