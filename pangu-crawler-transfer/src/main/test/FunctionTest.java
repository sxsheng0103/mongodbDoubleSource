import io.swagger.models.auth.In;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FunctionTest {

    /**
     * 请求T无返回
     * @param consumer
     */
    public static void test1(Consumer<Integer> consumer, Integer integer) {
        consumer.accept(integer);
    }

    /**
     * 无请求参数，返回T
     * @param supplier
     */
    public static void test2(Supplier<Integer> supplier){
        System.out.println(supplier.get());
    }

    /**
     * 请求T返回R
     * @param integer
     * @param function
     */
    public static void test3(Integer integer,Function<Integer,Integer> function){
        System.out.println(function.apply(integer));
    }


    public static void main(String[] args) {
        //test3(10,val->val*val);
    }
}

