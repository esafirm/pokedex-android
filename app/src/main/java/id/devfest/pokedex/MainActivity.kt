package id.devfest.pokedex

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProviders
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import id.devfest.pokedex.adapter.PokemonAdapter
import id.devfest.pokedex.di.Injectable
import id.devfest.pokedex.di.ViewModelFactory
import id.devfest.pokedex.model.Pokemon
import id.devfest.pokedex.utils.FullScreenUtils
import id.devfest.pokedex.utils.GridSpacingItemDecoration
import id.devfest.pokedex.utils.observe
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector, Injectable {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>

    override fun supportFragmentInjector(): AndroidInjector<androidx.fragment.app.Fragment> = fragmentDispatchingAndroidInjector

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    }

    var pokemonAdapter: PokemonAdapter? = null

    companion object {
        const val DIALOG_TAG = "Detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FullScreenUtils.apply(window)
        setContentView(R.layout.activity_main)
        setupToolbar()
        setupRecyclerView()

        fab.setOnClickListener {
            Toast.makeText(applicationContext, "Add Something!", Toast.LENGTH_SHORT).show()
        }

        viewModel.pokemonList.observe(this) {
            it?.let {
                updateAdapterList(it)
            }
        }

        viewModel.loadMoreResult.observe(this) {
            it?.let {
                when (it.first) {
                    is MainViewModel.Result.Success -> {
                        pokemonAdapter?.state = if (it.second) PokemonAdapter.State.LOADING else PokemonAdapter.State.NONE
                    }
                    is MainViewModel.Result.Error -> {
                        pokemonAdapter?.state = PokemonAdapter.State.RETRY
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { _, insets ->
            appbar.setPadding(
                appbar.paddingLeft,
                appbar.paddingTop + insets.systemWindowInsetTop,
                appbar.paddingRight,
                appbar.paddingBottom
            )
            insets.consumeSystemWindowInsets()
        }

        toolbar.setNavigationIcon(R.drawable.ic_menu)
        toolbar.setNavigationOnClickListener {
            drawer.openDrawer(GravityCompat.START)
        }
    }

    private fun showDetailDialog(pokemonId: Int) {
        val detailDialog = DetailDialog.newInstance(pokemonId)
        detailDialog.show(supportFragmentManager, DIALOG_TAG)
    }

    private fun setupRecyclerView() {
        val spanCount = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else 2

        pokemonRecycler.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, spanCount)
        pokemonAdapter = PokemonAdapter(listOf()) {
            showDetailDialog(it.id)
        }.apply {
            onLoadMore = {
                viewModel.triggerLoadMore(it)
            }

            onRetryClick = {
                this.state = PokemonAdapter.State.LOADING
            }

            state = PokemonAdapter.State.LOADING
        }

        pokemonRecycler.adapter = pokemonAdapter
        val spacingInPixel = resources.getDimensionPixelSize(R.dimen.spacingBetweenItem)
        pokemonRecycler.addItemDecoration(GridSpacingItemDecoration(spanCount, spacingInPixel, true, 0))
    }

    private fun updateAdapterList(pokemonList: List<Pokemon>) {
        pokemonAdapter?.let {
            it.pokemonList = pokemonList
            it.notifyDataSetChanged()
        }
    }

}
