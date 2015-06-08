# Compiler
Compiler for a subset of Pascal. Senior year coursework. The project lasted the entire semester.
The project was split into four parts: Lexical Analyzer, Parser, SymbolTable and Semantic Actions.
The final product was able to generate .tvi code (which could be run by The Vassar Interpreter) from Pascal code.

# To run
By running the parser driver class (in the drivers package) on a test file (in the semantictest package),
the compiler will output each semantic action computing (and the current stack).
Then, at the bottom, the corresponding .tvi code is shown.

# Part one: Lexical Analyzer
The lexical analyzer used a tokenizer to turn the Pascal code into a string of tokens.
This was tested by running the tokenizer on various input files.

# Part two: Parser
The parser used a top-down [LL(1)] method to parse through the grammar and put appropiate productions onto the stack.
Later, the implementation for semantic actions were added, so the final product uses the parser driver to generate tvi code.

# Part three: Symbol Table
The symbol table stores global and local variables.

# Part four: Semantic Actions
The 57 different semantic actions were written from pseudocode provided to the entire class.
The "Execute" method is called from the parser driver whenever a semantic action is processed, and the appropiate action is performed.
