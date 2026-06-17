package media.grab.os.extractor

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ExtractorModule {
    @Binds
    @Singleton
    abstract fun bindExtractor(impl: GenericExtractor): Extractor
}
