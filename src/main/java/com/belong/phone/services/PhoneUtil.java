package com.belong.phone.services;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;

import static java.util.Objects.isNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PhoneUtil {


  public static String convertStringToHex(String str) {

    // display in uppercase
    char[] chars = Hex.encodeHex(str.getBytes(StandardCharsets.UTF_8), false);
    return String.valueOf(chars);
  }

  public static String convertHexToString(String hex) {
    String result = "";
    try {
      byte[] bytes = Hex.decodeHex(hex);
      result = new String(bytes, StandardCharsets.UTF_8);
    } catch (DecoderException e) {
      throw new IllegalArgumentException("Invalid Hex format!");
    }
    return result;
  }
}
