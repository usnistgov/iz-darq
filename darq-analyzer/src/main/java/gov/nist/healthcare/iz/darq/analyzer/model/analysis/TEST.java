package gov.nist.healthcare.iz.darq.analyzer.model.analysis;

import java.util.HashMap;
import java.util.Objects;

public class TEST {

    public static class M {
        private String value;

        public M(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            M m = (M) o;
            return Objects.equals(value, m.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    public static void main(String[] args) {
        HashMap<M, String> map = new HashMap<>();
        map.put(new M("v1"), "value");

        System.out.println(map.containsKey(new M("v1")));
    }
}
