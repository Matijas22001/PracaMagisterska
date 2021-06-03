package pl.polsl.MathHelper.view_binding;

import android.content.Context;

import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;

public class HawkWrapper {
    public static void init(Context context) {
        NoEncryption encryption = new NoEncryption();
        Hawk.init(context).setEncryption(encryption).build();
    }
}

