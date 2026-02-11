package Engine;

import android.app.Activity;
import android.content.SharedPreferences;

public class Util
{
  // 전체 어플에 공용으로 적용되는 SharedPreferences에 Key, Value로 값을 저장한다. Value는 String이다.
  public static void setAppPreferences(Activity context, String key, String value)
  {
    SharedPreferences pref = null;
    pref = context.getSharedPreferences(C.LOG_TAG, 0);
    SharedPreferences.Editor prefEditor = pref.edit();
    prefEditor.putString(key, value);

    prefEditor.commit();
  }

  // 전체 어플에 공용으로 적용되는 SharedPreferences에서 String 값을 가져온다.  
  public static String getAppPreferences(Activity context, String key)
  {
    String returnValue = null;

    SharedPreferences pref = null;
    pref = context.getSharedPreferences(C.LOG_TAG, 0);

    returnValue = pref.getString(key, "");

    return returnValue;
  }
}