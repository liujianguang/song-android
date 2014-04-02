package com.song1.musicno1.entity;

/**
 * User: amongothers
 * Date: 13-6-13
 * Time: 下午2:56
 */
public class Utf8CodingFucker {
  public static String fuck(String input) {
    if(input == null || input.length() < 6 || !input.contains("\\u")) {
      return input;
    }
    TokenFucker tokenFucker = new TokenFucker();
    StringBuilder builder = new StringBuilder();
    String temp;
    for(int i = 0; i < input.length(); i++) {
      if((temp = tokenFucker.fuck(input.charAt(i))) != null) {
        builder.append(temp);
      }
    }
    return builder.toString();
  }

  static class TokenFucker {
    final TokenFuckerState STATE_INIT = new TokenFuckerInitState();
    final TokenFuckerState STATE_SLASH = new TokenFuckerSlashState();
    final TokenFuckerState STATE_U = new TokenFuckerUState();
    final TokenFuckerState STATE_1 = new TokenFucker1State();
    final TokenFuckerState STATE_2= new TokenFucker2State();
    final TokenFuckerState STATE_3= new TokenFucker3State();
    char[] cache = new char[6];
    TokenFuckerState state = STATE_INIT;

    //不断喂食字符，返回不为null的时候就是表示得到了一个可用的token
    public String fuck(char input) {
      return state.fuck(this, input);
    }

    abstract class TokenFuckerState {
      public abstract String fuck(TokenFucker fucker, char input);
      public boolean isHex(char input) {
        return ('0' <= input && input <= '9') || ('a' <= input && input <= 'f');
      }
    }

    class TokenFuckerInitState extends TokenFuckerState {

      @Override
      public String fuck(TokenFucker fucker, char input) {
        cache[0] = input;
        if(input != '\\') {
          return new String(cache, 0, 1);
        } else {
          fucker.state = STATE_SLASH;
          return null;
        }
      }
    }

    class TokenFuckerSlashState extends TokenFuckerState {

      @Override
      public String fuck(TokenFucker fucker, char input) {
        cache[1] = input;
        if(input != 'u') {
          fucker.state = STATE_INIT;
          return new String(cache, 0, 2);
        } else {
          fucker.state = STATE_U;
          return null;
        }
      }
    }

    class TokenFuckerUState extends TokenFuckerState {

      @Override
      public String fuck(TokenFucker fucker, char input) {
        cache[2] = input;
        if(!isHex(input)) {
          fucker.state = STATE_INIT;
          return new String(cache, 0, 3);
        } else {
          fucker.state = STATE_1;
          return null;
        }
      }
    }

    class TokenFucker1State extends TokenFuckerState {

      @Override
      public String fuck(TokenFucker fucker, char input) {
        cache[3] = input;
        if(!isHex(input)) {
          fucker.state = STATE_INIT;
          return new String(cache, 0, 4);
        } else {
          fucker.state = STATE_2;
          return null;
        }
      }
    }

    class TokenFucker2State extends TokenFuckerState {

      @Override
      public String fuck(TokenFucker fucker, char input) {
        cache[4] = input;
        if(!isHex(input)) {
          fucker.state = STATE_INIT;
          return new String(cache, 0, 5);
        } else {
          fucker.state = STATE_3;
          return null;
        }
      }
    }

    class TokenFucker3State extends TokenFuckerState {
      int[] codePoint = new int[1];

      @Override
      public String fuck(TokenFucker fucker, char input) {
        cache[5] = input;
        if(!isHex(input)) {
          fucker.state = STATE_INIT;
          return new String(cache, 0, 6);
        } else {
          fucker.state = STATE_INIT;
          String hexStr = new String(cache, 2, 4);
          codePoint[0] = Integer.parseInt(hexStr, 16);
          return new String(codePoint, 0, 1);
        }
      }
    }

  }
}
