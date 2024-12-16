package app.root.util;

import com.aspectran.utils.Assert;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.annotation.jsr305.Nullable;

/**
 * <p>Created: 2024-12-16</p>
 */
public class IPv6Util {

    private final static String FILTER_HEX = "[0-9a-fA-F]";

    public static boolean isValid(String ipAddress) {
        Assert.notNull(ipAddress, "host must not be null");
        int doubleIdx = ipAddress.indexOf("::");
        boolean compressed = doubleIdx != -1;
        String[] arr = ipAddress.split(":", -1);
        if (compressed) {
            if (ipAddress.endsWith(":")) {
                return false;
            }
            if (ipAddress.indexOf("::", doubleIdx + 1) != -1) {
                return false;  //too many ::
            }
            if (arr.length < 3 || arr.length > 8) {
                return false;
            }
        } else {
            if (arr.length != 8) {
                return false;
            }
        }
        try {
            for (String str : arr) {
                if (str.length() > 4) {
                    return false;
                }
                if (!str.equals(filter(str, FILTER_HEX))) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Nullable
    public static String normalize(String ipAddress) {
        if (!isValid(ipAddress)) {
            return null;
        }
        String[] arr = ipAddress.split(":", -1);
        short[] addr = new short[8];
        try {
            int idx = 0;
            for (String str : arr) {
                if (str.isEmpty()) {
                    //compressed field
                    if (idx == 0) {
                        //first field(s) are omitted (there will be two zero length fields)
                        idx++;
                    } else {
                        //middle fields(s) are omitted
                        idx = 8 - (arr.length - (idx + 1));
                    }
                } else {
                    addr[idx++] = toUnsignedShort(str, 16);
                }
            }
        } catch (Exception e) {
            return null;
        }
        return format(addr);
    }

    @NonNull
    private static String format(@NonNull short[] addr) {
        return String.format("%04x:%04x:%04x:%04x:%04x:%04x:%04x:%04x",
                addr[0] & 0xffff,
                addr[1] & 0xffff,
                addr[2] & 0xffff,
                addr[3] & 0xffff,
                addr[4] & 0xffff,
                addr[5] & 0xffff,
                addr[6] & 0xffff,
                addr[7] & 0xffff);
    }

    @NonNull
    private static String filter(@NonNull String str, String regex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            String ch = str.substring(i, i + 1);
            if (ch.matches(regex)) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static short toUnsignedShort(String str, int radix) throws NumberFormatException {
        int value = Integer.parseUnsignedInt(str, radix);
        if (value < 0 || value > 0xffff) {
            throw new NumberFormatException("UShort:value out of range");
        }
        return (short)value;
    }

    public static void main(String[] args) {
        System.out.println(normalize("0:0:0:0:0:0:0:1"));
        System.out.println(normalize("1:2:3:4:5:6:7:8"));
        System.out.println(normalize("1111:4444:7777:aaaa:bbbb:cccc:dddd:ffff"));
        System.out.println(normalize("1:2:3::8"));
        System.out.println(normalize("1:2::8"));
        System.out.println(normalize("::8"));
        System.out.println(normalize("::7:8"));
        System.out.println(normalize("1.1.1.1"));
        System.out.println(normalize("1111:4444:7777:aaaa:bbbb:cccc:dddd:ffff:"));
        System.out.println(normalize("1111:4444:7777:aaaa:bbbb:cccc:dddd::ffff"));
        System.out.println(normalize("1:2:3:::8"));
    }

}
