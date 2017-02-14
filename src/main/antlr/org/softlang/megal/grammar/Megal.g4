grammar Megal;
import JSON;

// Tokens that describe increase and decrease of indentation
tokens { INDENT, DEDENT }

// Setup the imports for the Denter Helper
@lexer::header {
  import com.yuvalshavit.antlr4.DenterHelper;
  import static org.softlang.megal.grammar.LiteralsHelperKt.acceptLiteral;
}

// Configure denter helper on new line
@lexer::members {
  private final DenterHelper denter = DenterHelper.builder()
    .nl(NL)
    .indent(MegalParser.INDENT)
    .dedent(MegalParser.DEDENT)
    .pullToken(MegalLexer.super::nextToken);

  @Override
  public Token nextToken() {
    return denter.nextToken();
  }
}

module:
    'module' ID NL declaration* group*;

submodule:
    'submodule' ID (NL | INDENT declaration* group* DEDENT);

group:
    DOC NL declaration+;

declaration:
    submodule | statement | imports;

statement:
    (ID '\'')? node node node cart* bind? (NL | INDENT (cont NL)* DEDENT);

bind:
    '=' node;

cart:
    '*' node;

cont:
    node node;

node:
    (primary | tuple | json | literal | op);

primary:
    abstr? ID (obj | array | tuple)?;

abstr:
    '?';

tuple:
    '(' (node (',' node)*)? ')';

literal:
    LITERAL;

op:
    '->' | ':->' | '<-' | '<-:' | '<->' | '<' | '>' | '=' | ':' | '<<' | '>>' |
     '|>>' | '|<<' | '>>|' | '<<|';

imports:
    'import' ID (NL | 'where' INDENT substitution* DEDENT);

substitution:
    ID 'sub' ID NL;

// Nested URLs
LITERAL:
    '<' (~'>')* '>' {acceptLiteral(getText())}?;

// Qualified identifier
ID: FRAG ('.' FRAG)*;

// Identifier text
fragment FRAG: [A-Za-z_] [A-Za-z0-9_\-]*;

// Documentation
DOC: '/*' .*? '*/';

// Newline with capturing whitespaces
NL: ('\r'? '\n' [ \t]*);

// Comments
COMMENT: ('//' (~[\n\r])*) -> skip;

// Non-significant newline
NSNL: '\\' '\r'? '\n' -> skip;

// Whitespaces
WS: [ \t] -> skip;