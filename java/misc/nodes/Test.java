class A {
}

class B extends A {
}

public class Test {
    public static void main(String[] args) {
        B b = new B();
        A a = b;
        B c = (B) a;
    }
}
