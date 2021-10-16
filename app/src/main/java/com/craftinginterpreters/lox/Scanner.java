package com.craftinginterpreters.lox;

import java.util.*;

import static com.craftinginterpreters.lox.TokenType.*;

class Scanner {
  private final String source;

  private final List<Token> tokens = new ArrayList<>();

  // first character of lexeme being scanned
  private int start = 0;
  // character being considered
  private int current = 0;
  // the source line, current is on
  private int line = 1;

  private static final Map<String, TokenType> keywords;

  static {
    keywords = new HashMap<>();
    keywords.put("and", AND);
    keywords.put("class", CLASS);
    keywords.put("else", ELSE);
    keywords.put("false", FALSE);
    keywords.put("for", FOR);
    keywords.put("fun", FUN);
    keywords.put("if", IF);
    keywords.put("nil", NIL);
    keywords.put("or", OR);
    keywords.put("print", PRINT);
    keywords.put("return", RETURN);
    keywords.put("super", SUPER);
    keywords.put("this", THIS);
    keywords.put("true", TRUE);
    keywords.put("var", VAR);
    keywords.put("while", WHILE);
  }

  Scanner(String source) {
    this.source = source;
  }

  List<Token> scanTokens() {
    while (!isAtEnd()) {
      // We are at the beginning of the next lexeme.
      start = current;
      scanToken();
    }
    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  private void scanToken() {
    char c = advance();
    switch (c) {
      case '(' -> addToken(LEFT_PAREN);
      case ')' -> addToken(RIGHT_PAREN);
      case '{' -> addToken(LEFT_BRACE);
      case '}' -> addToken(RIGHT_BRACE);
      case ',' -> addToken(COMMA);
      case '.' -> addToken(DOT);
      case '-' -> addToken(MINUS);
      case '+' -> addToken(PLUS);
      case ';' -> addToken(SEMICOLON);
      case '*' -> addToken(STAR);
      case '!' -> addToken(match('=') ? BANG_EQUAL : BANG);
      case '=' -> addToken(match('=') ? EQUAL_EQUAL : EQUAL);
      case '<' -> addToken(match('=') ? LESS_EQUAL : LESS);
      case '>' -> addToken(match('=') ? GREATER_EQUAL : GREATER);
      case '/' -> {
        if (match('/')) {
          while (peek() != '\n' && !isAtEnd()) advance();
        } else {
          addToken(SLASH);
        }
      }
      case ' ', '\t', '\r' -> {
      }
      case '\n' -> line++;

      case '"' -> string();

      // We keep scanning on error. There may be other errors
      // later in the program. It gives our users a better
      // experience if we detect as many of those as possible
      // in one go.
      default -> {
        if (isDigit(c)) {
          number();
        } else if (isAlpha(c)) {
          identifier();
        } else {
          Lox.error(line, "Unexpected character.");
        }
      }
    }
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }

  private char advance() {
    if (this.isAtEnd()) {
      throw new RuntimeException("Unexpected end of source");
    }
    return source.charAt(current++);
  }

  // One character lookahead
  private char peek() {
    if (isAtEnd()) return '\0';
    return source.charAt(current);
  }

  // two chars lookahead
  private char peekNext() {
    if (current + 1 >= this.source.length()) return '\0';
    return source.charAt(current + 1);
  }

  private boolean match(char c) {
    if (this.isAtEnd()) return false;
    if (this.source.charAt(current) != c) return false;
    current++;
    return true;
  }

  private boolean isAtEnd() {
    return current >= this.source.length();
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
  }

  private boolean isAlphaNumeric(char c) {
    return isDigit(c) || isAlpha(c);
  }

  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') line++;
      advance();
    }

    if (isAtEnd()) {
      Lox.error(line, "Unclosed string literal");
    }

    // closing '"'. peek() told us its there
    advance();

    String value = source.substring(start + 1, current - 1);
    addToken(STRING, value);
  }

  private void number() {
    while (isDigit(peek())) {
      advance();
    }

    if (peek() == '.' && isDigit(peekNext())) {
      advance();

      while (isDigit(peek())) {
        advance();
      }
    }

    addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
  }

  private void identifier() {
    while (isAlphaNumeric(peek())) advance();
    String text = source.substring(start, current);
    TokenType type = keywords.get(text);
    addToken(Objects.requireNonNullElse(type, IDENTIFIER));
  }

}
