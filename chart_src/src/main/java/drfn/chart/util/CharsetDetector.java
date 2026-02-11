package drfn.chart.util;

public class CharsetDetector {
  private static final String DEFAULT_CHARSET = "MS949";
  private static final int MS949_NORMAL = 0;
  private static final int MS949_2BYTE = 1;
  private static final int MS949_KSC_2BYTE = 2;
  private static final int UTF8_NORMAL = 0;
  private static final int UTF8_2BYTE = 1;
  private static final int UTF8_3BYTE = 2;
  private static final int UTF8_4BYTE = 3;

  public static String detect(byte[] bytes) {
    if (isUTF8(bytes))
      return "UTF-8";
    if (isMS949(bytes))
      return "MS949";
    return DEFAULT_CHARSET;
  }

  public static boolean isMS949(byte[] bytes) {
    int status = MS949_NORMAL;
    for (int ch : bytes) {
      if (status < 0)
        return false;

      switch (status) {
        case MS949_NORMAL:
          if (ch < 0x80)
            status = MS949_NORMAL;
          else if (0x81 <= ch && ch <= 0xc5)
            status = MS949_2BYTE;
          else if (0xc5 < ch && ch <= 0xfe)
            status = MS949_KSC_2BYTE;
          else
            status = -1;
          break;
        case MS949_2BYTE:
          if ((0x41 <= ch && ch <= 0x5a) || (0x61 <= ch && ch <= 0x7a)
            || (0x81 <= ch && ch <= 0xfe))
            status = MS949_NORMAL;
          else
            status = -1;
          break;
        case MS949_KSC_2BYTE:
          if (0xa1 <= ch && ch <= 0xfe)
            status = MS949_NORMAL;
          else
            status = -1;
          break;
        default:
          break;
      }
    }
    return true;
  }

  public static boolean isUTF8(byte[] bytes) {
    int status = UTF8_NORMAL;

    for (int ch : bytes) {
      if (status < 0)
        return false;

      switch (status) {
        case UTF8_NORMAL:
          if ((ch & 0x80) == 0)
            status = UTF8_NORMAL;
          else if (((ch & 0xe0) ^ 0xc0) == 0)
            status = UTF8_2BYTE;
          else if (((ch & 0xf0) ^ 0xe0) == 0)
            status = UTF8_3BYTE;
          else if (((ch & 0xf8) ^ 0xf0) == 0)
            status = UTF8_4BYTE;
          else
            status = -1;
          break;
        case UTF8_2BYTE:
          if (((ch & 0xc0) ^ 0x80) == 0)
            status = UTF8_NORMAL;
          else
            status = -1;
          break;
        case UTF8_3BYTE:
          if (((ch & 0xc0) ^ 0x80) == 0)
            status = UTF8_2BYTE;
          else
            status = -1;
          break;
        case UTF8_4BYTE:
          if (((ch & 0xc0) ^ 0x80) == 0)
            status = UTF8_3BYTE;
          else
            status = -1;
          break;
        default:
          break;
      }
    }
    return true;
  }
}
