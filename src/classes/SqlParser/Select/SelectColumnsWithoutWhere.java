package classes.SqlParser.Select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import classes.DBNode;
import classes.FileSystemFactory;
import classes.TabelImp;
import classes.XMLParser;
import interfaces.WriterInterface;

public class SelectColumnsWithoutWhere extends Select {

    String tableName;
    ArrayList<ArrayList<String>> y = new ArrayList<>();

    public SelectColumnsWithoutWhere(String input) {
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
            TabelImp table = parser.read(tableName);
            for (String visit : colms) {
                if (table.isIn(visit)) {
                    ArrayList<String> z = new ArrayList<>();
                    DBNode elements = table.getFromTable(table.getIndex(visit));
                    for (int i = 0; i < elements.getColumn().size(); i++) {
                        if (elements.getColumn().get(i) instanceof Date) {
                            z.add(new SimpleDateFormat("yyyy-mm-dd").format(elements.getColumn().get(i)));
                        } else {
                            z.add((elements.getColumn().get(i) != null) ? elements.getColumn().get(i).toString()
                                    : null);
                        }
                    }
                    y.add(z);
                } else
                    throw new RuntimeException("Column " + visit + "not found");
            }
            drawTable(colms, y);
            setColumms(colms);
            if (matcher.group(11) != null) {
                // there is order by
                String[] a = {""};
                return orderBy(y, divideComma(matcher.group(12)), a);
            }
            return y;
        } else
            throw new RuntimeException("Invalid Query for Select");
    }

    public ArrayList<ArrayList<String>> getSelected() {
        return y;
    }
}