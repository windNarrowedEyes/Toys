package com.haibara.toys.mybatis.plugin.process;

public class BeautifulSqlProcessor implements SqlProcessor {

  public static final int ORDER = -10;

  @Override
  public String process(String sql) throws Throwable {
    sql = sql + " ";
    StringBuilder sb = new StringBuilder();
    boolean addSpaces = false;
    boolean inQuote = false;
    char preCharacter = ' ';
    char character = sql.charAt(0);
    for (int i = 0; i < sql.length() - 1;) {
      char nextcharacter = sql.charAt(++i);
      if (inQuote) {
        if (character == '\'' && nextcharacter != '\'' && preCharacter != '\'') {
          inQuote = false;
        }
        sb.append(character);
      } else if (!Character.isWhitespace(character)) {
        if (character == '\'') {
          inQuote = true;
        }
        if (addSpaces) {
          sb.append(' ');
          addSpaces = false;
        }
        sb.append(character);
      } else {
        addSpaces = true;
      }
      preCharacter = character;
      character = nextcharacter;
    }
    return sb.toString();
  }

  @Override
  public int getOrder() {
    return ORDER;
  }
}
