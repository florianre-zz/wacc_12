package bindings;

public enum TypeLabels {

    ARRAY_L("Arr"),
    PAIR_L("Pr");

    private final String name;

    TypeLabels(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
