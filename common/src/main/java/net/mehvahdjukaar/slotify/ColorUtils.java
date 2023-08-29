package net.mehvahdjukaar.slotify;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Locale;

//ML class
public class ColorUtils {

    //utility codec that serializes either a string or an integer
    public static final Codec<Integer> CODEC = Codec.either(Codec.intRange(0, 0xffffffff),
            Codec.STRING.flatXmap(ColorUtils::isValidStringOrError, s->isValidStringOrError(s)
                    .map(ColorUtils::formatString))).xmap(
            either -> either.map(integer -> integer, s -> Integer.parseUnsignedInt(s, 16)),
            integer -> Either.right("#" + String.format("%08X", integer))
    );

    private static String formatString(String s){
        return "#"+ s.toUpperCase(Locale.ROOT);
    }

    public static DataResult<String> isValidStringOrError(String s) {
        String st = s;
        if (s.startsWith("0x")) {
            st = s.substring(2);
        } else if (s.startsWith("#")) {
            st = s.substring(1);
        }

        // Enforce the maximum length of eight characters (including prefix)
        if (st.length() > 8) {
            return DataResult.error( ()->"Invalid color format. Hex value must have up to 8 characters.");
        }

        try {
            int parsedValue = Integer.parseUnsignedInt(st, 16);
            return DataResult.success(st);
        } catch (NumberFormatException e) {
            return DataResult.error(()-> "Invalid color format. Must be in hex format (0xff00ff00, #ff00ff00, ff00ff00) or integer value");
        }
    }

    public static boolean isValidString(String s) {
        return isValidStringOrError(s).result().isPresent();
    }

}
