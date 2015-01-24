package demo.simple;

import com.fourinone.MulBean;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by lili on 15/1/24.
 * 测试国际化的例子
 */
public class TestLocale {

    public static void main(String[] args) {
//        String resourcesName = "config";
//
        String resourcesName = "META-INF/config"; //必须前面不能有/ 否则找不到文件
//        String    resourcesName ="/Users/lili/env/sources/github/fourinone/target/classes/META-INF/config";
//        return mb!=null?mb:
               // MulBean mb = new MulBean(null);

        ResourceBundle bundle = ResourceBundle.getBundle(resourcesName, Locale.getDefault());


//			bundle = ResourceBundle.getBundle(resourcesName);
        System.out.println("default locale");


    }
}
