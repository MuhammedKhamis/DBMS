package classes.SqlParser.Select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.regex.Pattern;

import classes.DBNode;
import classes.FileSystemFactory;
import classes.TabelImp;
import classes.XMLParser;
import interfaces.WriterInterface;

public class SelectColumnsWithWhere extends Select {

    String tableName;
    ArrayList<ArrayList<String>> x = new ArrayList<>();

    public SelectColumnsWithWhere(String input) {
        super(input);
    }

    @Override
    public ArrayList<ArrayList<String>> select() throws Exception {
        pattern = Pattern.compile(
                "(?i)\\s*select\\s+(.+)(\\s*,\\s*(\\w+))*\\s+from\\s+(\\w+)(\\s+where\\s+(\\w+)\\s*(\\W)\\s*(?:'(\\w+(\\s*\\w+)*)'|(\\d+)|\\d+.\\d+))?\\s*(order\\s+by\\s+((\\w+)(\\s*,\\s*(\\w+))*)\\s*((?:asc|desc))?)?;?");
        matcher = pattern.matcher(input);
        if (matcher.find()) {
            String columns = matcher.group(1);
            String[] colms = divideComma(columns);
            tableName = matcher.group(4);
            String colName = matcher.group(6);
            String condition = matcher.group(7);
            String rowName = (matcher.group(10) != null) ? matcher.group(10) : matcher.group(8);
            ArrayList<Integer> indexies = getIndexiesForWhere(tableName, colName, condition, rowName);
            TabelImp table = parser.read(tableName);
            for (String visit : colms) {
                if (table.isIn(visit)) {
                    DBNode elements = table.getFromTable(table.getIndex(visit));
                    if (condition.equals("=") || condition.equals(">") || condition.equals("<")) {
                        ArrayList<String> y = new ArrayList<>();
                        for (int i = 0; i < indexies.size(); i++) {
                            if (elements.getColumn().get(indexies.get(i)) instanceof Date) {
                                y.add(new SimpleDateFormat("yyyy-mm-dd")
                                        .format(elements.getColumn().get(indexies.get(i))));
                            } else {
                                y.add((elements.getColumn().get(indexies.get(i)) != null)
                                        ? elements.getColumn().get(indexies.get(i)).toString() : "null");
                            }
                        }
                        x.add(y);
                    } else {
                        throw new RuntimeException("Invalid Operator");
                    }
                } else {
                    throw new RuntimeException("Column " + visit + "not found");
                }
            }
            drawTable(colms, x);
            setColumms(colms);
            if (matcher.group(11) != null) {
                // there is order by
                String[] a = {"a"};
                return orderBy(x, divideComma(matcher.group(12)), a);
            }
            return x;
        } else
            throw new RuntimeException("Invalid Query for Select");
    }

    public ArrayList<ArrayList<String>> getSelected() {
        return x;
    }

}
