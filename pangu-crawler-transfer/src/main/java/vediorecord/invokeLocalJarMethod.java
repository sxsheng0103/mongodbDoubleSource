package vediorecord;

import org.reflections.Reflections;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author sheng.ding
 * @Date 2021/3/15 16:10
 * @Version 1.0
 **/
    public class invokeLocalJarMethod {
        static URLClassLoader classLoader = null;
        static Reflections reflections = new Reflections("com.holytax.message.service", classLoader);
        private static void init(){
            try{
                classLoader =
                        new URLClassLoader(
                                new URL[] {new URL("file:D:\\sb-business-message-1.4.0.jar")},
                                Thread.currentThread().getContextClassLoader());
            }catch (Exception e){
                System.out.println("读取录屏程序错误不存在");
            }
        }
//        static Set<Class<? extends YktdxService>> subTypesOf = reflections.getSubTypesOf(YktdxService.class);
//        //从继承中获取任意实现
//        private static YktdxService executeLocalMethod(){
//            YktdxService YktdxService = null;
//            AtomicReference<YktdxService> ins = new AtomicReference<>();
//            subTypesOf.stream().findAny().ifPresent(clazz -> {
//                try {
//                    ins.set(clazz.newInstance());
//                } catch (Exception e) {
//                    throw new RuntimeException();
//                }
//            });
//            return ins.get();
//        }
//
//        public static void main(String[] args) {
//            init();
//            executeLocalMethod().binding(null);
//        }
}
