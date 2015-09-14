import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import java.util.*;
/**
 * Created by NhanCao on 14-Sep-15.
 */
public class Main {
    public Main() {

    }

    public static void log(Object msg) {
        System.out.println(msg);
    }

    public static void main(String[] args) {

        Observable.just(1, 2, 3, 4).subscribe(System.out::println);

        Observable
                .just(1, 2, 3)
                .doOnNext(x -> {
                    if (x % 2 == 0) throw new RuntimeException("test");
                })
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("Completed Observable.");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.err.println("Whoops: " + throwable.getMessage());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("Got: " + integer);
                    }
                });

        Observable
                .just(1, 2, 3)
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        if (integer % 2 != 0) {
                            throw new RuntimeException("haha");
                        }
                    }
                })
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("Completed Observable.");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.err.println("Whoops: " + throwable.getMessage());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("Got: " + integer);
                    }
                });

//        Filter even numbers
        Observable
                .just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .filter(integer -> integer % 2 == 0)
                .subscribe(System.out::println);
// => 2, 4, 6, 8, 10
//        Iterating with "forEach"
        Observable
                .just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .forEach(System.out::println);
// => 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
//        Group by
        Observable
                .just(1, 2, 3, 4, 5)
                .groupBy(integer -> integer % 2 == 0).subscribe(grouped -> {
                grouped.toList().subscribe(integers -> {
                System.out.println(integers + " (Even: " + grouped.getKey() + ")");
            });
        });
        // [1, 3, 5] (Even: false)
        // [2, 4] (Even: true)
//        Take only the first N values emitted
        Observable
                .just(1, 2, 3, 4, 5)
                .take(2)
                .subscribe(System.out::println);

        // => 1, 2
//       First
        Observable
                .just(1, 2, 3, 4, 5)
                .first()
                .subscribe(System.out::println);

        // => 1
//        Last
        Observable
                .just(1, 2, 3, 4, 5)
                .last()
                .subscribe(System.out::println);

        // => 5
//        Distinct
        Observable
                .just(1, 2, 1, 3, 4, 2)
                .distinct()
                .subscribe(System.out::println);

        // => 1, 2, 3, 4
//        Map()
        Observable.just("Hello world!")
                .map(s -> s.hashCode())
                .subscribe(i -> log(Integer.toString(i)));

        // => 121287312
//        Iterate an array list
        List<User> users = new ArrayList<>();

        users.add(new User("jon snow"));
        users.add(new User("tyrion lannister"));

        Observable
                .just(users)
                .concatMap(userList -> Observable.from(userList))
                .subscribe(user -> log(user.name));

        // concatMap: when applied to an item emitted by the source Observable, returns an Observable

        // => "jon snow", "tyrion lannister"
    }
    static class User{
        String name;
        public User(String name) {
            this.name=name;
        }
    }
}
