package classes.SqlParser.Select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.sql.SQLException;
import java.util.regex.Pattern;

import classes.FileSystemFactory;
import classes.TabelImp;
import classes.XMLParser;
import interfaces.WriterInterface;

public class SelectAllWithWhere extends Select {

    String tableName;

    public SelectAllWithWhere(String input) {
        super(input);
    }

    @Override
    public ArrayList<ArrayList<String>> select() throws Exception {
        pattern = Pattern.compile(
                "(?i)\\s*select\\s+(\\*)(\\s*,\\s*(\\w+))*\\s+from\\s+(\\w+)(\\s+where\\s+(\\w+)\\s*(\\W)\\s*(?:'(\\w+(\\s*\\w+)*)'|(\\d+)|\\d+.\\d+))?\\s*(order\\s+by\\s+((\\w+)\\s*(?:asc|desc)?(\\s*,\\s*(\\w+)\\s*(?:asc|desc)?)*)\\s*)?(union)?;?");
        matcher = pattern.matcher(input);
        ArrayList<ArrayList<ArrayList<String>>> resu = new ArrayList<>();
        while (matcher.find()) {
            tableName = matcher.group(4);
            String colName = matcher.group(6);
            String condition = matcher.group(7);
            String rowName = (matcher.group(10) != null) ? matcher.group(10)
                    : (matcher.group(8) != null) ? matcher.group(8) : matcher.group(11);
            ArrayList<Integer> indexies = getIndexiesForWhere(tableName, colName, condition, rowName);
            TabelImp table = parser.read(tableName);
            if (condition.equals("=") || condition.equals(">") || condition.equals("<")) {
                ArrayList<ArrayList<String>> x = new ArrayList<>();
                for (int i = 0; i < table.GetTableSize(); i++) {
                    ArrayList<String> y = new ArrayList<>();
                    for (int j : indexies) {
                        if (table.getFromTable(i).getColumn().get(j) instanceof Date) {
                            y.add(new SimpleDateFormat("yyyy-mm-dd").format(table.getFromTable(i).getColumn().get(j)));
                        } else {
                            y.add((table.getFromTable(i).getColumn().get(j) != null)
                                    ? table.getFromTable(i).getColumn().get(j).toString() : null);
                        }
                    }
                    x.add(y);
                }
                drawTable(table.getCoulmnNames(), x);
                setColumms(table.getCoulmnNames());
                if (matcher.group(11) != null) {
                    // there is order by
                    String[] a = {"sad"};
                    return orderBy(x, divideComma(matcher.group(12)), a);
                }
                // return x;
                resu.add(x);
            } else {
                throw new RuntimeException("Invalid Operator");
            }
        }
        if (resu.size() == 2) {
            if (resu.get(0).size() == resu.get(1).size()) {
                ArrayList<ArrayList<String>> conca = new ArrayList<>();
                conca.addAll(resu.get(0));
                for (int i = 0; i < resu.get(1).size(); i++) {
                    for (int j = 0; j < resu.get(1).get(0).size(); j++) {
                        conca.get(i).add(resu.get(1).get(i).get(j));
                    }
                }
                return ellimenate(conca);
            }
        } else if (resu.size() == 1) {
            return resu.get(0);
        } else {
            throw new RuntimeException();
        }
        throw new RuntimeException();
    }
}
