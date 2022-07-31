import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.AsyncSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.ReplaySubject
import java.io.File
import java.io.FileNotFoundException
import kotlin.math.pow
import kotlin.math.roundToInt

fun main() {
    //-----------------------------------------------------------------------------------
    exampleOf("AsyncSubject") {
        val subscriptions = CompositeDisposable()
        val asyncSubject = AsyncSubject.create<Int>()

        subscriptions.add(asyncSubject.subscribeBy(
            onNext = { println("1) $it") },
            onComplete = { println("1) Complete") }
        ))

        asyncSubject.onNext(0)
        asyncSubject.onNext(1)
        asyncSubject.onNext(2)
        println("1) call Complete")
        asyncSubject.onComplete()

        subscriptions.dispose()
    }
}

fun arxiv() {
    //-----------------------------------------------------------------------------------
    exampleOf("ReplaySubject") {

        val subscriptions = CompositeDisposable()

        val replaySubject = ReplaySubject.createWithSize<String>(2)

        replaySubject.onNext("1")
        replaySubject.onNext("2")
        replaySubject.onNext("3")

        subscriptions.add(replaySubject.subscribeBy(
            onNext = { println("1) $it") },
            onError = { println("1) $it") }
        ))

        subscriptions.add(replaySubject.subscribeBy(
            onNext = { println("2) $it") },
            onError = { println("2) $it") }
        ))

        replaySubject.onNext("4")

        subscriptions.add(replaySubject.subscribeBy(
            onNext = { println("3) $it") },
            onError = { println("3) $it") })
        )

        replaySubject.onError(RuntimeException("Error!"))
    }
    //-----------------------------------------------------------------------------------
    exampleOf("BehaviorSubject State") {

        val subscriptions = CompositeDisposable()

        val behaviorSubject = BehaviorSubject.createDefault(0)

        println(behaviorSubject.value)

        subscriptions.add(behaviorSubject.subscribeBy {
            println("1) $it")
        }
        )

        behaviorSubject.onNext(1)

        println(behaviorSubject.value)

        subscriptions.dispose()
    }
    //-----------------------------------------------------------------------------------
    exampleOf("BehaviorSubject") {

        val subscriptions = CompositeDisposable()

        val behaviorSubject = BehaviorSubject.createDefault("Initial value")

        behaviorSubject.onNext("X")

        val subscriptionOne = behaviorSubject.subscribeBy(
            onNext = { println("1) $it") },
            onError = { println("1) $it") }
        )

        behaviorSubject.onNext("Y")

        behaviorSubject.onError(RuntimeException("Error!"))

        subscriptions.add(behaviorSubject.subscribeBy(
            onNext = { println("2) $it") },
            onError = { println("2) $it") }
        )
        )
    }

//-----------------------------------------------------------------------------------
    exampleOf("PublishSubject") {
        val publishSubject = PublishSubject.create<Int>()

        publishSubject.onNext(1)

        val subscriptionOne = publishSubject.subscribe { int ->
            println("1) $int")
        }

        publishSubject.onNext(2)

        val subscriptionTwo = publishSubject.subscribe { int ->
            println("2) $int")
        }

        publishSubject.onNext(3)

        subscriptionOne.dispose()

        publishSubject.onNext(4)

        publishSubject.onComplete()

        publishSubject.onNext(5)

        subscriptionTwo.dispose()

        val subscriptionThree = publishSubject.subscribeBy(
            onNext = { println("3) $it") },
            onComplete = { println("3) Complete") }
        )

        publishSubject.onNext(6)
    }

//-----------------------------------------------------------------------------------
    exampleOf("never") {
        val observable = Observable.never<Any>()
            .doOnSubscribe { println("doOnSubscibe") }
            .doOnComplete { println("doOnComplete") }
            .doOnDispose { println("doOnDispose") }

        val observer = observable.subscribeBy(
            onNext = { println(it) },
            onComplete = { println("Completed") }
        )

        observer.dispose()

    }

//-----------------------------------------------------------------------------------
    exampleOf("Single") {
        val subscriptions = CompositeDisposable()

        fun loadText(filename: String): Single<String> {

            return Single.create create@{ emitter ->
                val file = File(filename)

                if (!file.exists()) {
                    emitter.onError(FileNotFoundException("Can’t find $filename"))
                    return@create
                }

                val contents = file.readText(Charsets.UTF_8)

                emitter.onSuccess(contents)
            }
        }

        val observer = loadText("build.gradle")
            .subscribeBy(
                onSuccess = { println(it) },
                onError = { println("Error, $it") }
            )

        subscriptions.add(observer)
    }

//-----------------------------------------------------------------------------------
    exampleOf("CompositeDisposable") {
        val subscriptions = CompositeDisposable()

        val disposable = Observable.just("A", "B", "C")
            .subscribe {
                println(it)
            }

        subscriptions.add(disposable)
        //subscriptions.dispose()
    }

//-----------------------------------------------------------------------------------
    exampleOf("dispose") {
        val mostPopular: Observable<String> = Observable.just("A", "B", "C")

        val subscription = mostPopular.subscribe {
            println(it)
        }

        subscription.dispose()
    }

//-----------------------------------------------------------------------------------
    exampleOf("range") {
        val observable: Observable<Int> = Observable.range(1, 10)

        observable.subscribe {
            val n = it.toDouble()
            val fibonacci = ((1.61803.pow(n) - 0.61803.pow(n)) / 2.23606).roundToInt()
            println(fibonacci)
        }

    }

//-----------------------------------------------------------------------------------
    exampleOf("empty") {
        val observable = Observable.empty<Unit>()

        observable.subscribeBy(
            onNext = { println(it) },
            onComplete = { println("Completed") }
        )
    }
//-----------------------------------------------------------------------------------
    exampleOf("subscribe") {
        val observable = Observable.just(1, 2, 3)
        val zzz = observable.subscribe { println(it) }
        println(zzz)
    }

//-----------------------------------------------------------------------------------
    exampleOf("fromIterable") {
        val observable: Observable<Int> = Observable.fromIterable(listOf(1, 2, 3))
    }

//-----------------------------------------------------------------------------------
    exampleOf("just3") {
        val observable: Observable<List<Int>> = Observable.just(listOf(1, 2, 3))
    }

//-----------------------------------------------------------------------------------
    exampleOf("just2") {
        val observable: Observable<Int> = Observable.just(1, 2, 3)
    }

//-----------------------------------------------------------------------------------
    exampleOf("just1") {
        val observable: Observable<Int> = Observable.just(1)
    }

}

fun exampleOf(description: String, action: () -> Unit) {
    println("\n--- Example of: $description ---")
    action()
}