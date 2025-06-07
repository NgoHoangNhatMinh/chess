SRC_DIR := src
OUT_DIR := out

SOURCE := $(shell find $(SRC_DIR) -name "*.java") 

# Compile and run
all:
	@echo "Compiling program..."
	@mkdir -p $(OUT_DIR)
	@javac -d $(OUT_DIR) $(SOURCE)
	@echo "Starting program..."
	@java -cp $(OUT_DIR) Main

compile:
	@echo "Compiling program..."
	@mkdir -p $(OUT_DIR)
	@javac -d $(OUT_DIR) $(SOURCE)

run:
	@echo "Starting program..."
	@java -cp $(OUT_DIR) Main

clean:
	@echo "Cleaning folder..."
	@rm -rf $(OUT_DIR)

test:
	@echo "Running tests..."
	@mkdir -p $(OUT_DIR)
	@javac -d $(OUT_DIR) $(SOURCE)
	@java -cp $(OUT_DIR) Perft $(DEPTH)

engine:
	@echo "Running engine..."
	@mkdir -p $(OUT_DIR)
	@javac -d $(OUT_DIR) $(SOURCE)
	@java -cp $(OUT_DIR) Engine $(DEPTH)

play:
	@echo "Playing against engine..."
	@mkdir -p $(OUT_DIR)
	@javac -d $(OUT_DIR) $(SOURCE)
	@java -cp $(OUT_DIR) Main $(COLOR)