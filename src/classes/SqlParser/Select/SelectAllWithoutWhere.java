package classes.SqlParser.Select;

import java.util.ArrayList;
import java.util.regex.Pattern;

import classes.FileSystemFactory;
import classes.TabelImp;
import interfaces.WriterInterface;

public class SelectAllWithoutWhere extends Select {

    String tableName;

    public SelectAllWithoutWhere(String input) {
        super(input);
    }

    @Override
    public ArrayList<ArrayList<String>> select() throws Exception {
        pattern = Pattern.compile(
                "(?i)\\s*select\\s+(.+)(\\s*,\\s*(\\w+))*\\s+from\\s+(\\w+)(\\s+where\\s+(\\w+)\\s*(\\W)\\s*(?:'(\\w+(\\s*\\w+)*)'|(\\d+)|\\d+.\\d+))?\\s*(order\\s+by\\s+((\\w+)\\s*(?:asc|desc)?(\\s*,\\s*(\\w+)\\s*(?:asc|desc)?)*)\\s*)?;?");
        matcher = pattern.matcher(input);
        if (matcher.find()) {
            tableName = matcher.group(4);
            TabelImp table = parser.read(tableName);
            setColumms(table.getCoulmnNames());
            if (matcher.group(11) != null) {
                // there is order by
                String[] orderColumn = divideComma(matcher.group(12));
                String retColum[] = new String[orderColumn.length];
                String rettype[] = new String[orderColumn.length];
                for (int i = 0; i < orderColumn.length; i++) {
                    String[] test = dividespace(orderColumn[i]);
                    retColum[i] = test[0];
                    rettype[i] = test[1];
                }
                return orderBy(LastReturn(tableName), retColum, rettype);
            }
            return LastReturn(tableName);
        } else {
            throw new RuntimeException("Invalid Query for Select");
        }
    }
}
