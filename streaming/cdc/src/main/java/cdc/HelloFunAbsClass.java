package cdc;

public abstract class HelloFunAbsClass {

    private int base;

    public HelloFunAbsClass() {
        this.base = 10;
    }

    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }

    public abstract void handler(int x);

}
