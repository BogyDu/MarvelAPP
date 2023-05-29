package b.gaytan.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import b.gaytan.app.data.local.MarvelDao
import b.gaytan.app.data.local.MarvelDatabase
import b.gaytan.app.data.remote.Service
import b.gaytan.app.di.Module
import b.gaytan.app.repository.MarvelRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config
import retrofit2.Retrofit

@RunWith(AndroidJUnit4::class)
@Config(manifest= Config.NONE)
class ExampleUnitTest {

    var client:OkHttpClient? = null
    var retrofit:Retrofit? = null
    var database:MarvelDatabase? = null
    var context: Context? = null


    @Mock
    var api:Service? = null
    @Mock
    var dao:MarvelDao? = null

    @Before
    fun onBefore(){
        MockitoAnnotations.initMocks(this)

        context = ApplicationProvider.getApplicationContext()
        client = Module.provideOkHttpCLient()
        retrofit = Module.provideRetrofit(client!!)
        api =  Module.provideServicesApi(retrofit!!)
        database = Module.provideMarvelDatabase(context!!)
        dao = Module.provideMarvelDao(database!!)
    }
    @Test
    fun `test conection`() {
        assert(retrofit!!.baseUrl().toUrl().toString() == Constants.BASE_URL)
    }

    @Test
    fun `test api`() {
        val actualRepos = runBlocking {
            api!!.list("Spider-Man")
        }

       assert(actualRepos.isSuccessful)
    }

    @Test
    fun `test flow`(){
         runBlocking {
            var item1 =  MarvelRepository(api!!, dao!!).getAll().first()

            assert(item1.isEmpty())
        }

    }
}