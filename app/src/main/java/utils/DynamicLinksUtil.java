package utils;

import android.net.Uri;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class DynamicLinksUtil {

    public static Uri generateContentLink() {
        Uri baseUrl = Uri.parse("https://play.google.com/store");
        String domain = "https://krivec.page.link";

        DynamicLink link = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setLink(baseUrl)
                .setDomainUriPrefix(domain)
                //.setIosParameters(new DynamicLink.IosParameters.Builder("com.your.bundleid").build())
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("si.krivec.tracker").build())
                .buildDynamicLink();

        return link.getUri();
    }
}
