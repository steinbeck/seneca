/*
 * Copyright (c) 2012. John May <jwmay@sf.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package seneca.predictor.nmrshiftdb;

import com.google.common.collect.*;

import java.util.*;

/**
 * Represents the several spheres of a hose code in a single class. The spheres can be accessed by
 * the {@link #getSpheres(int i, int n)} and
 * {@link #getSpheres(int n)} methods.
 *
 * @author Kalai
 */
public class HOSECode {

    private final List<String> spheres;

    /**
     * Create a new HOSEcode from it's string representation
     *
     * @param code the hose code to create
     */
    public HOSECode(String code) {

        StringTokenizer tokenizer = new StringTokenizer(code, "()/");
        List<String> allSpheres = new ArrayList<String>();

        while (tokenizer.hasMoreTokens()) {
            allSpheres.add(tokenizer.nextToken());
        }
        this.spheres = Collections.unmodifiableList(allSpheres);
    }

    public HOSECode(List<String> spheres) {
        this.spheres = Collections.unmodifiableList(spheres);
    }

    public List<String> getSpheres() {
        return this.spheres;
    }

    /**
     * Access the number of stored spheres.
     *
     * @return
     */
    public int size() {
        return spheres.size();
    }

    public List<String> getSpheres(int i, int n) {
        if (n > spheres.size()) {
            throw new IllegalArgumentException("not enough spheres");
        }
        return spheres.subList(i, i + n);
    }

    public List<String> getSpheres(int n) {
        return getSpheres(0, n);
    }

    
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HOSECode hoseCode = (HOSECode) o;

        return !(spheres != null ? !spheres.equals(hoseCode.spheres) : hoseCode.spheres != null);
    }

    
    public int hashCode() {
        return spheres != null ? spheres.hashCode() : 0;
    }

    
    public String toString() {

        StringBuilder sb = new StringBuilder();

        int n = spheres.size();

        for (int i = 0; i < n; i++) {
            sb.append(spheres.get(i));
            sb.append(getSeparator(i));
        }

        return sb.toString();

    }

    private static char getSeparator(int i) {
        if (i == 0) {
            return '(';
        }
        if (i == 3) {
            return ')';
        }
        return '/';
    }

    public static void main(String[] args) {
        HashMap<HOSECode, Double> table = Maps.newHashMap();
        Multimap<HOSECode, Double> myMultimap = ArrayListMultimap.create();
        HOSECode code = new HOSECode("=CCO(CC,=CC,/=CC,C,C&,=OC/&O,CO,&CO,=OC,,=&C),,&,C,=OC,,,=&C,=C/,CO,,,=CO,&/");
        HOSECode code2 = new HOSECode("CCO(CC,=CC,/=CC,C,C&,=OC/&O,CO,&CO,=OC,,=&C),,&,C,=OC,,,=&C,=C/,CO,,,=CO,&/");
        HOSECode code3 = new HOSECode("=CCO(CC,=CC,/=CC,C,C&,=OC/");
        System.out.println("total size: " + code.size());
        List<String> spheres = code.getSpheres();
        for (String s : spheres) {
            System.out.println(s);
        }
//            table.put(code,1.0);
//            table.put(code2, 2.0);
//            System.out.println("Map size: " + table.size());

        System.out.println("code sphere 3: " + code.getSpheres(3));
        System.out.println("code 3 spheres : " + code3.getSpheres());
        System.out.println("equals: " + code.getSpheres(2).equals(code2.getSpheres(2)));

        myMultimap.put(code, 1.0);
        myMultimap.put(code2, 2.0);
        myMultimap.put(code3, 2.0);

        System.out.println("multimap size: " + myMultimap.size());

    }

    private class HOSECodeTable {

        private SetMultimap<List<String>, HOSECode> table = HashMultimap.create();

        public boolean put(HOSECode code) {
            boolean changed = false;
            for (int n = 1; n < code.size(); n++)
                changed = table.put(code.getSpheres(n), code) || changed;
            return changed;
        }

        public Set<HOSECode> get(List<String> sphere) {
            return table.get(sphere);
        }

        public Set<HOSECode> get(HOSECode code) {
            return get(code.getSpheres(code.size()));
        }

        public Set<HOSECode> get(String code) {
            return get(new HOSECode(code));
        }

    }
}
