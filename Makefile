JAVAC=javac
sources = $(wildcard src/main/*.java)
classes = $(addprefix bin/main/, $(notdir $(sources:.java=.class)))
tests = $(wildcard src/test/*.java)
test_classes = $(addprefix bin/test/, $(notdir $(tests:.java=.class)))

all: $(classes)

compiletest: $(test_classes)

clean:
	rm -f $(classes) $(test_classes)

bin/main/%.class: src/main/%.java
	$(JAVAC) -cp src/main/ -d bin/main/ $<

bin/test/%.class: src/test/%.java
	$(JAVAC) -cp src/test/:bin/main/:lib/junit-4.12.jar -d bin/test/ $<
