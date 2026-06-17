package media.grab.os.common

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val throwable: Throwable, val message: String? = null) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> this
    is Result.Loading -> this
}
