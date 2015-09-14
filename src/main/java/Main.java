import rx.Observable;
import rx.Subscriber;

/**
 * Created by NhanCao on 14-Sep-15.
 */
public class Main {
    public Main() {

    }
    public static void main(String[] args){
        Observable.just(1, 2, 3, 4).subscribe(x->{
            System.out.println(x);
        });

        Observable
                .just(1, 2, 3)
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


    }
}
